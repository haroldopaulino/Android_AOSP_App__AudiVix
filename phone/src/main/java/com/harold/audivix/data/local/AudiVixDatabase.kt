package com.harold.audivix.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.harold.audivix.data.model.AppSettings
import com.harold.audivix.data.model.MediaType

class AudiVixDatabase(context: Context) : SQLiteOpenHelper(context, "audivix_room.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE settings(id INTEGER PRIMARY KEY CHECK(id = 1), endpoint TEXT NOT NULL, darkTheme INTEGER NOT NULL, notifications INTEGER NOT NULL, chimes INTEGER NOT NULL, offlineDownloads INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE media_order(mediaId TEXT PRIMARY KEY, mediaType TEXT NOT NULL, sortOrder INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE cached_file(url TEXT PRIMARY KEY, localPath TEXT NOT NULL, kind TEXT NOT NULL, updatedAt INTEGER NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS cached_file(url TEXT PRIMARY KEY, localPath TEXT NOT NULL, kind TEXT NOT NULL, updatedAt INTEGER NOT NULL)")
        }
    }

    fun readSettings(defaultEndpoint: String): AppSettings {
        readableDatabase.rawQuery("SELECT endpoint,darkTheme,notifications,chimes,offlineDownloads FROM settings WHERE id = 1", null).use { cursor ->
            if (cursor.moveToFirst()) {
                return AppSettings(
                    endpoint = cursor.getString(0),
                    darkTheme = cursor.getInt(1) == 1,
                    notificationsEnabled = cursor.getInt(2) == 1,
                    chimesEnabled = cursor.getInt(3) == 1,
                    offlineDownloadsEnabled = cursor.getInt(4) == 1
                )
            }
        }
        val settings = AppSettings(endpoint = defaultEndpoint)
        saveSettings(settings)
        return settings
    }

    fun saveSettings(settings: AppSettings) {
        val values = ContentValues().apply {
            put("id", 1)
            put("endpoint", settings.endpoint)
            put("darkTheme", if (settings.darkTheme) 1 else 0)
            put("notifications", if (settings.notificationsEnabled) 1 else 0)
            put("chimes", if (settings.chimesEnabled) 1 else 0)
            put("offlineDownloads", if (settings.offlineDownloadsEnabled) 1 else 0)
        }
        writableDatabase.insertWithOnConflict("settings", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun readOrder(type: MediaType): List<String> {
        val ids = mutableListOf<String>()
        readableDatabase.rawQuery("SELECT mediaId FROM media_order WHERE mediaType = ? ORDER BY sortOrder ASC", arrayOf(type.name)).use { cursor ->
            while (cursor.moveToNext()) ids += cursor.getString(0)
        }
        return ids
    }

    fun saveOrder(type: MediaType, ids: List<String>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("media_order", "mediaType = ?", arrayOf(type.name))
            ids.forEachIndexed { index, id ->
                val values = ContentValues().apply {
                    put("mediaId", id)
                    put("mediaType", type.name)
                    put("sortOrder", index)
                }
                db.insertWithOnConflict("media_order", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun cachedPath(url: String): String? {
        readableDatabase.rawQuery("SELECT localPath FROM cached_file WHERE url = ?", arrayOf(url)).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getString(0) else null
        }
    }

    fun saveCachedPath(url: String, path: String, kind: String) {
        val values = ContentValues().apply {
            put("url", url)
            put("localPath", path)
            put("kind", kind)
            put("updatedAt", System.currentTimeMillis())
        }
        writableDatabase.insertWithOnConflict("cached_file", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deleteCachedPath(url: String) {
        writableDatabase.delete("cached_file", "url = ?", arrayOf(url))
    }
}
