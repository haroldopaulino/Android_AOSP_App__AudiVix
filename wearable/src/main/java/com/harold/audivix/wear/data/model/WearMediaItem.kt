package com.harold.audivix.wear.data.model

import com.google.gson.annotations.SerializedName

const val AUDIVIX_WEAR_ENDPOINT = "https://sparqm.com/audivix/web_services/"
const val AUDIVIX_WEAR_STREAM_API_KEY = "TOP_SECRET_API_KEY"

data class WearMediaCatalogResponse(
    val media: List<WearMediaDto>? = null,
    val items: List<WearMediaDto>? = null,
    val data: List<WearMediaDto>? = null,
    val results: List<WearMediaDto>? = null
) {
    fun allItems(): List<WearMediaDto> = media ?: items ?: data ?: results ?: emptyList()
}

data class WearMediaDto(
    val id: Long? = null,
    @SerializedName("media_id") val mediaId: Long? = null,
    val title: String? = null,
    val artist: String? = null,
    val type: String? = null,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("thumbnail_url") val thumbnailUrlSnake: String? = null,
    @SerializedName("thumbnailUrl") val thumbnailUrlCamel: String? = null,
    @SerializedName("artwork_url") val artworkUrlSnake: String? = null,
    @SerializedName("artworkUrl") val artworkUrlCamel: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("stream_token") val streamToken: String? = null,
    @SerializedName("stream_url") val streamUrlSnake: String? = null,
    @SerializedName("streamUrl") val streamUrlCamel: String? = null,
    @SerializedName("duration_ms") val durationMs: Long? = null,
    @SerializedName("duration_seconds") val durationSeconds: Long? = null
)

data class WearMediaItem(
    val id: String,
    val title: String,
    val artist: String,
    val type: WearMediaType,
    val thumbnailUrl: String,
    val artworkUrl: String,
    val streamUrl: String,
    val durationLabel: String
)

enum class WearMediaType {
    Audio,
    Video
}

fun WearMediaDto.toWearMedia(baseEndpoint: String): WearMediaItem {
    val resolvedId = mediaId ?: id ?: 0L
    val rawType = (mediaType ?: type ?: "audio").lowercase()
    val resolvedType = if (rawType.contains("video")) WearMediaType.Video else WearMediaType.Audio
    val artwork = artworkUrlSnake ?: artworkUrlCamel ?: imageUrl ?: thumbnailUrlSnake ?: thumbnailUrlCamel ?: ""
    val thumbnail = thumbnailUrlSnake ?: thumbnailUrlCamel ?: artwork
    val stream = resolveWearStreamUrl(baseEndpoint, resolvedId, streamUrlSnake, streamUrlCamel, streamToken)
    val millis = durationMs ?: ((durationSeconds ?: 0L) * 1000L)

    return WearMediaItem(
        id = resolvedId.toString(),
        title = title?.takeIf { it.isNotBlank() } ?: "Untitled",
        artist = artist?.takeIf { it.isNotBlank() } ?: "Unknown Artist",
        type = resolvedType,
        thumbnailUrl = thumbnail,
        artworkUrl = artwork,
        streamUrl = stream,
        durationLabel = millis.toWearDuration()
    )
}

private fun resolveWearStreamUrl(
    baseEndpoint: String,
    resolvedId: Long,
    streamUrlSnake: String?,
    streamUrlCamel: String?,
    streamToken: String?
): String {
    val base = baseEndpoint.normalizedWearBase()
    val rawUrl = when {
        !streamUrlSnake.isNullOrBlank() -> streamUrlSnake
        !streamUrlCamel.isNullOrBlank() -> streamUrlCamel
        !streamToken.isNullOrBlank() -> base + "media_stream.php?token=" + streamToken
        else -> base + "media_stream.php?id=" + resolvedId
    }
    return rawUrl.withWearApiKey()
}

private fun String.normalizedWearBase(): String = if (endsWith("/")) this else "$this/"

private fun String.withWearApiKey(): String {
    if (contains("api_key=")) return this
    val separator = if (contains("?")) "&" else "?"
    return this + separator + "api_key=" + AUDIVIX_WEAR_STREAM_API_KEY
}

private fun Long.toWearDuration(): String {
    if (this <= 0L) return "--:--"
    val totalSeconds = this / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}
