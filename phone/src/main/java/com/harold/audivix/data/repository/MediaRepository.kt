package com.harold.audivix.data.repository

import com.harold.audivix.data.api.ApiFactory
import com.harold.audivix.data.local.AudiVixDatabase
import com.harold.audivix.data.model.AUDIVIX_PLATFORM
import com.harold.audivix.data.model.AudiVixMedia
import com.harold.audivix.data.model.AudiVixPlaylist
import com.harold.audivix.data.model.MediaEventRequest
import com.harold.audivix.data.model.MediaType
import com.harold.audivix.data.model.PlaylistDeleteRequest
import com.harold.audivix.data.model.PlaylistItemDto
import com.harold.audivix.data.model.PlaylistSaveRequest
import com.harold.audivix.data.model.RatingPostRequest
import com.harold.audivix.data.model.toDomain
import com.harold.audivix.data.model.withCachedArtwork
import com.harold.audivix.data.model.withCachedMedia
import com.harold.audivix.data.model.withCachedThumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class MediaRepository(
    private val settingsRepository: SettingsRepository,
    private val cacheRepository: CacheRepository,
    private val database: AudiVixDatabase,
    private val deviceId: String = "android-audivix-device"
) {
    private suspend fun endpoint() = settingsRepository.settings.first().endpoint
    private suspend fun api() = ApiFactory.create(endpoint())

    suspend fun getCatalog(type: MediaType? = null, search: String? = null): List<AudiVixMedia> = withContext(Dispatchers.IO) {
        val typeParam = when (type) { MediaType.Audio -> "audio"; MediaType.Video -> "video"; null -> null }
        val base = endpoint()
        val response = api().getMedia(type = typeParam, search = search)
        val media = (response.items ?: response.media ?: response.data ?: response.results).orEmpty().map { dto ->
            val item = dto.toDomain(base)
            val thumbPath = cacheRepository.cachedPath(item.thumbnailUrl) ?: cacheRepository.cacheImage(item.thumbnailUrl)
            item.withCachedThumbnail(thumbPath)
        }
        applySavedOrder(type, media)
    }

    suspend fun prepareForPlayback(media: AudiVixMedia, downloadMedia: Boolean = true): AudiVixMedia {
        val artworkPath = cacheRepository.cachedPath(media.artworkUrl) ?: cacheRepository.cacheImage(media.artworkUrl)
        val withArtwork = media.withCachedArtwork(artworkPath)
        if (media.type == MediaType.Video) {
            return withArtwork.copy(localMediaPath = null)
        }
        return if (downloadMedia) {
            val mediaPath = cacheRepository.cachedMediaPath(media.streamUrl) ?: cacheRepository.cacheMedia(media.streamUrl)
            withArtwork.withCachedMedia(mediaPath)
        } else {
            withArtwork
        }
    }

    suspend fun preloadNext(media: AudiVixMedia) {
        prepareForPlayback(media, downloadMedia = true)
    }

    suspend fun downloadAll(media: List<AudiVixMedia>) {
        media.forEach { prepareForPlayback(it, downloadMedia = true) }
    }

    fun saveOrder(type: MediaType, ids: List<String>) = database.saveOrder(type, ids)

    private fun applySavedOrder(type: MediaType?, media: List<AudiVixMedia>): List<AudiVixMedia> {
        val mediaType = type ?: return media
        val saved = database.readOrder(mediaType)
        if (saved.isEmpty()) return media
        val order = saved.withIndex().associate { it.value to it.index }
        return media.sortedWith(compareBy<AudiVixMedia> { order[it.id] ?: Int.MAX_VALUE }.thenBy { it.title })
    }

    suspend fun getPlaylists(): List<AudiVixPlaylist> {
        val response = api().getPlaylists(deviceId = deviceId, platform = AUDIVIX_PLATFORM)
        return (response.playlists ?: response.items ?: response.data).orEmpty().map { it.toDomain() }
    }

    suspend fun savePlaylist(name: String, itemIds: List<String>): AudiVixPlaylist = api().savePlaylist(
        PlaylistSaveRequest(null, deviceId, name = name, items = itemIds.mapIndexedNotNull { index, id -> id.toLongOrNull()?.let { PlaylistItemDto(it, index + 1) } })
    ).toDomain()

    suspend fun deletePlaylist(playlistId: Long) { api().deletePlaylist(PlaylistDeleteRequest(playlistId, deviceId)) }

    suspend fun sendMediaEvent(mediaId: Long, eventType: String, positionMs: Long, durationMs: Long) {
        api().postMediaEvent(MediaEventRequest(mediaId, eventType, positionMs / 1000L, durationMs / 1000L, deviceId))
    }

    suspend fun postRating(mediaId: Long, rating: Int) { api().postRating(RatingPostRequest(mediaId, rating, deviceId)) }
}
