package com.harold.audivix.data.model

import com.squareup.moshi.Json

const val AUDIVIX_DEFAULT_ENDPOINT = "https://sparqm.com/audivix/web_services/"
const val AUDIVIX_PLATFORM = "android"
const val AUDIVIX_STREAM_API_KEY = "TOP_SECRET_API_KEY"

data class AppSettings(
    val endpoint: String = AUDIVIX_DEFAULT_ENDPOINT,
    val darkTheme: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val chimesEnabled: Boolean = true,
    val offlineDownloadsEnabled: Boolean = false,
    val showMiniPlayerForVideos: Boolean = true,
    val autoPlayNextMedia: Boolean = true
)

data class ApiStatusResponse(
    val ok: Boolean? = null,
    val success: Boolean? = null,
    val status: String? = null,
    val message: String? = null
)

data class MediaItemDto(
    val id: Long? = null,
    @Json(name = "media_id") val mediaId: Long? = null,
    val title: String? = null,
    val artist: String? = null,
    val description: String? = null,
    val type: String? = null,
    val category: String? = null,
    val genre: String? = null,
    @Json(name = "stream_url") val streamUrlSnake: String? = null,
    @Json(name = "streamUrl") val streamUrlCamel: String? = null,
    @Json(name = "stream_token") val streamToken: String? = null,
    @Json(name = "thumbnail_url") val thumbnailUrlSnake: String? = null,
    @Json(name = "thumbnailUrl") val thumbnailUrlCamel: String? = null,
    @Json(name = "artwork_url") val artworkUrlSnake: String? = null,
    @Json(name = "artworkUrl") val artworkUrlCamel: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "cover_url") val coverUrl: String? = null,
    @Json(name = "preview_video_url") val previewVideoUrlSnake: String? = null,
    @Json(name = "previewVideoUrl") val previewVideoUrlCamel: String? = null,
    @Json(name = "duration_seconds") val durationSeconds: Long? = null,
    @Json(name = "duration_ms") val durationMsSnake: Long? = null,
    @Json(name = "durationMs") val durationMsCamel: Long? = null,
    val rating: Double? = null,
    @Json(name = "average_rating") val averageRating: Double? = null,
    @Json(name = "play_count") val playCountSnake: Long? = null,
    @Json(name = "watch_count") val watchCount: Long? = null,
    @Json(name = "listen_count") val listenCount: Long? = null,
    @Json(name = "compressed_format") val compressedFormatSnake: String? = null,
    @Json(name = "format") val format: String? = null,
    @Json(name = "mime_type") val mimeType: String? = null
)

data class MediaCatalogResponse(
    val items: List<MediaItemDto>? = null,
    val media: List<MediaItemDto>? = null,
    val data: List<MediaItemDto>? = null,
    val results: List<MediaItemDto>? = null,
    val ok: Boolean? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class PlaylistItemDto(@Json(name = "media_id") val mediaId: Long, @Json(name = "sort_order") val sortOrder: Int)

data class PlaylistDto(
    val id: Long? = null,
    @Json(name = "playlist_id") val playlistId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    @Json(name = "is_public") val isPublic: Boolean? = null,
    val items: List<PlaylistItemDto>? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

data class PlaylistListResponse(
    val playlists: List<PlaylistDto>? = null,
    val items: List<PlaylistDto>? = null,
    val data: List<PlaylistDto>? = null,
    val ok: Boolean? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class PlaylistSaveRequest(
    @Json(name = "playlist_id") val playlistId: Long? = null,
    @Json(name = "device_id") val deviceId: String,
    val platform: String = AUDIVIX_PLATFORM,
    val name: String,
    val description: String = "",
    @Json(name = "is_public") val isPublic: Boolean = false,
    val items: List<PlaylistItemDto>
)

data class PlaylistDeleteRequest(@Json(name = "playlist_id") val playlistId: Long, @Json(name = "device_id") val deviceId: String, val platform: String = AUDIVIX_PLATFORM)

data class MediaEventRequest(
    @Json(name = "media_id") val mediaId: Long,
    @Json(name = "event_type") val eventType: String,
    @Json(name = "position_seconds") val positionSeconds: Long,
    @Json(name = "duration_seconds") val durationSeconds: Long,
    @Json(name = "device_id") val deviceId: String,
    val platform: String = AUDIVIX_PLATFORM,
    @Json(name = "app_version") val appVersion: String = "1.0.14"
)

data class RatingPostRequest(@Json(name = "media_id") val mediaId: Long, val rating: Int, @Json(name = "device_id") val deviceId: String, val platform: String = AUDIVIX_PLATFORM)

fun isVideoMedia(type: String, mimeType: String?, format: String?, streamUrl: String?): Boolean {
    val combined = listOf(type, mimeType.orEmpty(), format.orEmpty(), streamUrl.orEmpty()).joinToString(" ").lowercase()
    return combined.contains("video") || combined.contains("mp4") || combined.contains("m4v") || combined.contains("webm") || combined.contains("mov")
}
