package com.harold.audivix.data.repository

import android.content.Context
import android.webkit.MimeTypeMap
import com.harold.audivix.data.local.AudiVixDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest

class CacheRepository(private val context: Context, private val database: AudiVixDatabase) {
    private val client = OkHttpClient.Builder().build()
    private val imageDir = File(context.filesDir, "audivix/images").apply { mkdirs() }
    private val mediaDir = File(context.filesDir, "audivix/media").apply { mkdirs() }

    suspend fun cacheImage(url: String?): String? = cache(url, imageDir, "image")
    suspend fun cacheMedia(url: String?): String? = cache(url, mediaDir, "media")

    fun cachedPath(url: String?): String? = validCachedPath(url, minimumBytes = 1L)

    fun cachedMediaPath(url: String?): String? = validCachedPath(url, minimumBytes = 64 * 1024L)

    fun forgetCachedPath(url: String?) {
        if (url.isNullOrBlank()) return
        database.cachedPath(url)?.let { path ->
            runCatching { File(path.removePrefix("file://")).delete() }
        }
        database.deleteCachedPath(url)
    }

    private fun validCachedPath(url: String?, minimumBytes: Long): String? {
        if (url.isNullOrBlank()) return null
        val path = database.cachedPath(url) ?: return null
        val file = File(path.removePrefix("file://"))
        if (!file.exists() || !file.canRead() || file.length() < minimumBytes) {
            runCatching { file.delete() }
            database.deleteCachedPath(url)
            return null
        }
        return path
    }

    private suspend fun cache(url: String?, directory: File, kind: String): String? = withContext(Dispatchers.IO) {
        if (url.isNullOrBlank()) return@withContext null
        if (kind != "media") cachedPath(url)?.let { return@withContext it } else cachedMediaPath(url)?.let { return@withContext it }
        val ext = extension(url, kind)
        val file = File(directory, "${url.sha256()}.$ext")
        if (!file.exists() || file.length() == 0L) {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body ?: return@withContext null
                file.outputStream().use { output -> body.byteStream().use { input -> input.copyTo(output) } }
            }
        }
        if (kind == "media" && file.length() < 64 * 1024L) {
            runCatching { file.delete() }
            database.deleteCachedPath(url)
            return@withContext null
        }
        val path = file.toURI().toString()
        database.saveCachedPath(url, path, kind)
        path
    }

    private fun extension(url: String, kind: String): String {
        val clean = url.substringBefore('?').substringAfterLast('/', "")
        val ext = clean.substringAfterLast('.', "").lowercase().takeIf { it.length in 2..5 }
        return ext ?: if (kind == "image") "jpg" else "media"
    }

    private fun String.sha256(): String = MessageDigest.getInstance("SHA-256").digest(toByteArray()).joinToString("") { "%02x".format(it) }
}
