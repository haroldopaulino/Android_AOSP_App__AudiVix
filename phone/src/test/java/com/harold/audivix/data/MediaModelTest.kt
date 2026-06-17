package com.harold.audivix.data

import com.harold.audivix.data.model.AUDIVIX_DEFAULT_ENDPOINT
import com.harold.audivix.data.model.MediaItemDto
import com.harold.audivix.data.model.MediaType
import com.harold.audivix.data.model.toDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaModelTest {
    @Test
    fun audioTypeMapsToAudio() {
        val dto = MediaItemDto(id = 1, title = "Song", type = "audio")
        assertEquals(MediaType.Audio, dto.toDomain(AUDIVIX_DEFAULT_ENDPOINT).type)
    }

    @Test
    fun videoTypeMapsToVideo() {
        val dto = MediaItemDto(id = 1, title = "Clip", type = "video")
        assertEquals(MediaType.Video, dto.toDomain(AUDIVIX_DEFAULT_ENDPOINT).type)
    }

    @Test
    fun dtoMapsTitle() {
        val dto = MediaItemDto(id = 1, title = "Song", artist = "Artist", type = "audio")
        assertEquals("Song", dto.toDomain(AUDIVIX_DEFAULT_ENDPOINT).title)
    }

    @Test
    fun dtoMapsArtist() {
        val dto = MediaItemDto(id = 1, title = "Song", artist = "Artist", type = "audio")
        assertEquals("Artist", dto.toDomain(AUDIVIX_DEFAULT_ENDPOINT).artist)
    }

    @Test
    fun streamUrlIncludesApiKey() {
        val dto = MediaItemDto(id = 9, title = "Song", type = "audio")
        assertTrue(dto.toDomain(AUDIVIX_DEFAULT_ENDPOINT).streamUrl.contains("api_key=TOP_SECRET_API_KEY"))
    }
}
