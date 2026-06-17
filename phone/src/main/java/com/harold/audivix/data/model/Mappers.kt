package com.harold.audivix.data.model

fun MediaItemDto.toDomain(baseEndpoint: String): AudiVixMedia {
    val resolvedId = mediaId ?: id ?: 0L
    val resolvedType = type.orEmpty().lowercase().ifBlank { "audio" }
    val rawStream = streamUrlSnake ?: streamUrlCamel ?: streamToken?.let { token ->
        "${baseEndpoint.normalizedBase()}media_stream.php?token=$token"
    } ?: "${baseEndpoint.normalizedBase()}media_stream.php?id=$resolvedId"
    val stream = rawStream.withStreamApiKey()
    val durationMs = durationMsSnake ?: durationMsCamel ?: ((durationSeconds ?: 0L) * 1000L)
    val artwork = artworkUrlSnake ?: artworkUrlCamel ?: imageUrl ?: coverUrl
    val thumb = thumbnailUrlSnake ?: thumbnailUrlCamel ?: artwork
    return AudiVixMedia(
        id = resolvedId.toString(),
        numericId = resolvedId,
        title = title ?: "Untitled",
        artist = artist.orEmpty(),
        subtitle = artist.orEmpty(),
        description = description.orEmpty(),
        type = if (isVideoMedia(resolvedType, mimeType, format ?: compressedFormatSnake, rawStream)) MediaType.Video else MediaType.Audio,
        streamUrl = stream,
        streamToken = streamToken,
        thumbnailUrl = thumb,
        artworkUrl = artwork ?: thumb,
        previewVideoUrl = previewVideoUrlSnake ?: previewVideoUrlCamel,
        durationMs = durationMs,
        rating = rating ?: averageRating ?: 0.0,
        playCount = playCountSnake ?: watchCount ?: listenCount ?: 0L,
        genre = genre ?: category ?: "General",
        compressedFormat = compressedFormatSnake ?: format ?: mimeType ?: if (isVideoMedia(resolvedType, mimeType, format, rawStream)) "MP4/H.264" else "AAC/Opus"
    )
}

fun AudiVixMedia.withCachedThumbnail(path: String?) = copy(localThumbnailPath = path)
fun AudiVixMedia.withCachedArtwork(path: String?) = copy(localArtworkPath = path)
fun AudiVixMedia.withCachedMedia(path: String?) = copy(localMediaPath = path)

fun PlaylistDto.toDomain(): AudiVixPlaylist {
    val resolvedId = playlistId ?: id ?: 0L
    return AudiVixPlaylist(resolvedId.toString(), resolvedId, name ?: "Playlist", items.orEmpty().map { it.mediaId.toString() })
}

fun String.normalizedBase(): String = if (endsWith("/")) this else "$this/"

fun String.withStreamApiKey(): String {
    if (!contains("media_stream.php", ignoreCase = true)) return this
    if (contains("api_key=", ignoreCase = true)) return this
    val separator = if (contains("?")) "&" else "?"
    return "$this${separator}api_key=$AUDIVIX_STREAM_API_KEY"
}
