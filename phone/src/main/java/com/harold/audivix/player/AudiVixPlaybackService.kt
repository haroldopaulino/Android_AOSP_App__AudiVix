package com.harold.audivix.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.harold.audivix.MainActivity
import com.harold.audivix.R
import java.io.File
import java.net.URL
import kotlin.concurrent.thread

class AudiVixPlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: Player
    private var currentArtworkUri: Uri? = null
    private var currentArtwork: Bitmap? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        player = AudiVixPlayerHolder.player(this)
        val sessionIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionIntent)
            .build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                publishMediaNotificationOnMain()
            }

            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                currentArtwork = null
                currentArtworkUri = mediaItem?.mediaMetadata?.artworkUri
                loadArtwork(currentArtworkUri)
                publishMediaNotificationOnMain()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                publishMediaNotificationOnMain()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                if (player.isPlaying) player.pause() else player.play()
            }
            ACTION_PREVIOUS -> {
                if (player.mediaItemCount > 0) {
                    if (player.hasPreviousMediaItem()) player.seekToPreviousMediaItem()
                    else player.seekTo(player.mediaItemCount - 1, 0L)
                    player.play()
                }
            }
            ACTION_NEXT -> {
                if (player.mediaItemCount > 0) {
                    if (player.hasNextMediaItem()) player.seekToNextMediaItem()
                    else player.seekTo(0, 0L)
                    player.play()
                }
            }
            ACTION_STOP -> {
                player.pause()
                stopForeground(STOP_FOREGROUND_DETACH)
            }
        }
        publishMediaNotificationOnMain()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    private fun publishMediaNotificationOnMain() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            publishMediaNotification()
        } else {
            mainHandler.post { publishMediaNotification() }
        }
    }

    private fun publishMediaNotification() {
        val session = mediaSession ?: return
        val currentItem = player.currentMediaItem ?: run {
            startForeground(NOTIFICATION_ID, emptyForegroundNotification())
            return
        }
        val metadata = currentItem.mediaMetadata
        val title = metadata.title?.toString().orEmpty().ifBlank { "AudiVix" }
        val artist = metadata.artist?.toString().orEmpty().ifBlank { "Unknown artist" }
        val artworkUri = metadata.artworkUri

        if (artworkUri != null && artworkUri != currentArtworkUri) {
            currentArtworkUri = artworkUri
            currentArtwork = null
            loadArtwork(artworkUri)
        }

        val views = playerViews(title, artist)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_audivix_legacy)
            .setContentTitle(title)
            .setContentText(artist)
            .setSubText("AudiVix")
            .setLargeIcon(currentArtwork)
            .setCustomContentView(views)
            .setCustomBigContentView(views)
            .setCustomHeadsUpContentView(views)
            .setContentIntent(session.sessionActivity)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setPublicVersion(publicLockScreenNotification(title, artist))
            .setOnlyAlertOnce(true)
            .setOngoing(player.isPlaying)
            .setShowWhen(false)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()

        startForeground(NOTIFICATION_ID, notification)
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    private fun emptyForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_audivix_legacy)
            .setContentTitle("AudiVix")
            .setContentText("")
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .build()
    }

    private fun playerViews(title: String, artist: String): RemoteViews {
        val views = RemoteViews(packageName, R.layout.notification_audivix_player)
        views.setTextViewText(R.id.notification_title, title)
        views.setTextViewText(R.id.notification_artist, artist)
        views.setImageViewBitmap(R.id.notification_artwork, scaledArtwork(currentArtwork ?: fallbackArtwork()))
        val playPauseIcon = if (player.playWhenReady && player.playbackState != Player.STATE_ENDED) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        views.setImageViewResource(R.id.notification_play_pause, playPauseIcon)
        views.setImageViewResource(R.id.notification_previous, android.R.drawable.ic_media_previous)
        views.setImageViewResource(R.id.notification_next, android.R.drawable.ic_media_next)
        views.setOnClickPendingIntent(R.id.notification_play_pause, pendingAction(ACTION_PLAY_PAUSE))
        views.setOnClickPendingIntent(R.id.notification_previous, pendingAction(ACTION_PREVIOUS))
        views.setOnClickPendingIntent(R.id.notification_next, pendingAction(ACTION_NEXT))
        val duration = player.duration.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val position = player.currentPosition.coerceAtLeast(0L).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        views.setProgressBar(R.id.notification_progress, duration, position, duration <= 0)
        return views
    }

    private fun publicLockScreenNotification(title: String, artist: String): Notification {
        val views = playerViews(title, artist)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_audivix_legacy)
            .setContentTitle(title)
            .setContentText(artist)
            .setLargeIcon(currentArtwork)
            .setCustomContentView(views)
            .setCustomBigContentView(views)
            .setContentIntent(mediaSession?.sessionActivity)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
    }

    private fun pendingAction(action: String): PendingIntent {
        val intent = Intent(this, AudiVixPlaybackService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun loadArtwork(uri: Uri?) {
        if (uri == null) return
        thread {
            val bitmap = runCatching {
                val value = uri.toString()
                when {
                    uri.scheme == "file" -> BitmapFactory.decodeFile(File(uri.path.orEmpty()).absolutePath)
                    uri.scheme == "http" || uri.scheme == "https" -> URL(value).openStream().use(BitmapFactory::decodeStream)
                    uri.scheme.isNullOrBlank() && value.startsWith("/") -> BitmapFactory.decodeFile(value)
                    else -> contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
                }
            }.getOrNull()
            if (bitmap != null) {
                mainHandler.post {
                    currentArtwork = bitmap
                    publishMediaNotification()
                }
            }
        }
    }

    private fun scaledArtwork(source: Bitmap): Bitmap {
        return runCatching {
            Bitmap.createScaledBitmap(source, 960, 384, true)
        }.getOrDefault(source)
    }

    private fun fallbackArtwork(): Bitmap {
        val bitmap = Bitmap.createBitmap(960, 384, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.BLACK)
        return bitmap
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "AudiVix Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "AudiVix media playback controls"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "audivix_playback"
        private const val NOTIFICATION_ID = 1002
        const val ACTION_PLAY_PAUSE = "com.harold.audivix.PLAY_PAUSE"
        const val ACTION_PREVIOUS = "com.harold.audivix.PREVIOUS"
        const val ACTION_NEXT = "com.harold.audivix.NEXT"
        const val ACTION_STOP = "com.harold.audivix.STOP"
    }
}
