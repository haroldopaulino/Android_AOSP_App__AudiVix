package com.harold.audivix.settings

import com.harold.audivix.data.model.AppSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsValidationTest {
    private fun normalizeEndpoint(value: String): String {
        val trimmed = value.trim()
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }

    @Test
    fun endpointKeepsTrailingSlashWhenPresent() {
        assertEquals("https://sparqm.com/audivix/web_services/", normalizeEndpoint("https://sparqm.com/audivix/web_services/"))
    }

    @Test
    fun endpointAddsTrailingSlashWhenMissing() {
        assertEquals("https://sparqm.com/audivix/web_services/", normalizeEndpoint("https://sparqm.com/audivix/web_services"))
    }

    @Test
    fun endpointTrimRemovesWhitespace() {
        assertEquals("https://sparqm.com/audivix/web_services/", normalizeEndpoint(" https://sparqm.com/audivix/web_services/ "))
    }

    @Test
    fun defaultThemeIsDark() {
        assertTrue(AppSettings().darkTheme)
    }

    @Test
    fun offlineDownloadsDefaultToDisabled() {
        assertFalse(AppSettings().offlineDownloadsEnabled)
    }
}
