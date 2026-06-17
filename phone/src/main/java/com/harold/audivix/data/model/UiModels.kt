package com.harold.audivix.data.model

import android.net.Uri

enum class MediaType { Audio, Video }

data class AudiVixMedia(
    val id: String,
    val numericId: Long,
    val title: String,
    val artist: String,
    val subtitle: String,
    val description: String,
    val type: MediaType,
    val streamUrl: String,
    val streamToken: String? = null,
    val thumbnailUrl: String? = null,
    val localThumbnailPath: String? = null,
    val artworkUrl: String? = null,
    val localArtworkPath: String? = null,
    val localMediaPath: String? = null,
    val previewVideoUrl: String? = null,
    val durationMs: Long,
    val rating: Double,
    val playCount: Long,
    val genre: String,
    val compressedFormat: String
) {
    val thumbnailModel: Any? get() = localThumbnailPath?.let(Uri::parse) ?: thumbnailUrl ?: artworkModel
    val artworkModel: Any? get() = localArtworkPath?.let(Uri::parse) ?: artworkUrl
    val playbackUri: String get() = localMediaPath ?: streamUrl
    val isVideo: Boolean get() = type == MediaType.Video || listOf(streamUrl, localMediaPath.orEmpty(), previewVideoUrl.orEmpty(), compressedFormat).joinToString(" ").lowercase().let { it.contains("video") || it.contains("mp4") || it.contains("m4v") || it.contains("webm") || it.contains("mov") }
}

data class AudiVixPlaylist(val id: String, val numericId: Long, val name: String, val itemIds: List<String>)
