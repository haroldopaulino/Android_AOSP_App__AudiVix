package com.harold.audivix.player

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import java.io.File
import com.harold.audivix.data.model.AudiVixMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerController(private val context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state
    private val player = AudiVixPlayerHolder.player(context)
    private var currentMedia: AudiVixMedia? = null
    private var queue: List<AudiVixMedia> = emptyList()
    private var preloadCallback: ((AudiVixMedia) -> Unit)? = null
    private var lastPreloadedId: String? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) = publishState()

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val mediaId = mediaItem?.mediaId.orEmpty()
                currentMedia = queue.firstOrNull { it.id == mediaId } ?: currentMedia
                publishState()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                publishState()
            }

            override fun onPlayerError(error: PlaybackException) {
                retryRemoteStreamAfterLocalFailure()
            }
        })
        startTicker()
    }

    fun play(media: AudiVixMedia, queue: List<AudiVixMedia> = emptyList(), onPreloadNext: ((AudiVixMedia) -> Unit)? = null) {
        val resolvedQueue = if (queue.isEmpty()) listOf(media) else queue
        val startIndex = resolvedQueue.indexOfFirst { it.id == media.id }.takeIf { it >= 0 } ?: 0
        this.currentMedia = resolvedQueue[startIndex]
        this.queue = resolvedQueue
        this.preloadCallback = onPreloadNext
        this.lastPreloadedId = null
        val mediaItems = resolvedQueue.map { queueItem ->
            MediaItem.Builder()
                .setUri(playbackUriFor(queueItem))
                .setMediaId(queueItem.id)
                .setMediaMetadata(metadataFor(queueItem))
                .build()
        }
        player.apply {
            setMediaItems(mediaItems, startIndex, 0L)
            prepare()
            play()
        }
        startPlaybackService()
        publishState()
    }

    fun player(): Player = player

    private fun startPlaybackService() {
        ContextCompat.startForegroundService(
            context,
            Intent(context, AudiVixPlaybackService::class.java)
        )
    }

    private fun playbackUriFor(media: AudiVixMedia): String {
        if (media.isVideo) return media.streamUrl
        val localPath = media.localMediaPath ?: return media.streamUrl
        if (!localPath.startsWith("file://")) return localPath
        val file = File(Uri.parse(localPath).path.orEmpty())
        return if (file.exists() && file.canRead() && file.length() >= 64 * 1024L) localPath else media.streamUrl
    }

    private fun metadataFor(media: AudiVixMedia): MediaMetadata {
        val artwork = media.localArtworkPath ?: media.artworkUrl
        val builder = MediaMetadata.Builder()
            .setTitle(media.title)
            .setArtist(media.artist.ifBlank { "Unknown artist" })
            .setAlbumTitle("AudiVix")
        artworkUriFor(artwork)?.let { uri ->
            builder.setArtworkUri(uri)
        }
        return builder.build()
    }

    private fun artworkUriFor(value: String?): Uri? {
        if (value.isNullOrBlank()) return null
        return when {
            value.startsWith("file://") -> Uri.parse(value)
            value.startsWith("/") -> Uri.fromFile(File(value))
            else -> Uri.parse(value)
        }
    }

    fun toggle() {
        if (player.isPlaying) player.pause() else player.play()
        publishState()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        publishState()
    }

    fun stop() {
        player.stop()
        player.clearMediaItems()
        currentMedia = null
        publishState()
    }

    fun playNext() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
            player.play()
            publishState()
            return
        }
        val next = queue.firstOrNull() ?: return
        play(next, queue, preloadCallback)
    }

    fun playPrevious() {
        if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
            player.play()
            publishState()
            return
        }
        val previous = queue.lastOrNull() ?: return
        play(previous, queue, preloadCallback)
    }

    private fun retryRemoteStreamAfterLocalFailure() {
        val failed = currentMedia ?: return
        val localPath = failed.localMediaPath ?: return
        if (!localPath.startsWith("file://")) return
        runCatching { File(Uri.parse(localPath).path.orEmpty()).delete() }
        val remote = failed.copy(localMediaPath = null)
        currentMedia = remote
        queue = queue.map { if (it.id == remote.id) remote else it }
        val item = MediaItem.Builder()
            .setUri(remote.streamUrl)
            .setMediaId(remote.id)
            .setMediaMetadata(metadataFor(remote))
            .build()
        player.apply {
            setMediaItem(item)
            prepare()
            play()
        }
        startPlaybackService()
        publishState()
    }

    private fun startTicker() {
        scope.launch {
            while (true) {
                publishState()
                maybePreloadNext()
                delay(500)
            }
        }
    }

    private fun maybePreloadNext() {
        val state = _state.value
        val current = state.current ?: return
        if (state.durationMs <= 0L) return
        val remaining = state.durationMs - state.positionMs
        if (remaining in 1..10_000L) {
            val index = queue.indexOfFirst { it.id == current.id }
            val next = queue.getOrNull(index + 1) ?: queue.firstOrNull() ?: return
            if (lastPreloadedId != next.id) {
                lastPreloadedId = next.id
                preloadCallback?.invoke(next)
            }
        }
    }

    private fun publishState() {
        val mediaId = player.currentMediaItem?.mediaId
        if (!mediaId.isNullOrBlank()) {
            currentMedia = queue.firstOrNull { it.id == mediaId } ?: currentMedia
        }
        _state.value = PlayerState(
            current = currentMedia,
            isPlaying = player.isPlaying,
            positionMs = player.currentPosition.coerceAtLeast(0),
            durationMs = player.duration.coerceAtLeast(currentMedia?.durationMs ?: 0),
            bufferedMs = player.bufferedPosition.coerceAtLeast(0)
        )
    }
}
