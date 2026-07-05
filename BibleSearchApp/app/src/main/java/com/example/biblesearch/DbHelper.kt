package com.example.biblesearch

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.FileOutputStream

/**
 * Opens the read-only "kjv.db" that is bundled inside assets/.
 * Android can't query a database directly from assets, so on first
 * launch we copy it into the app's private storage, then reuse that
 * copy on every future launch. Everything happens fully offline.
 */
class DbHelper(private val context: Context) {

    private val dbName = "kjv_v2.db"
    private var database: SQLiteDatabase? = null

    fun open(): SQLiteDatabase {
        database?.let { return it }

        val dbFile = context.getDatabasePath(dbName)
        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()
            context.assets.open(dbName).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        val db = SQLiteDatabase.openDatabase(
            dbFile.path, null, SQLiteDatabase.OPEN_READONLY
        )
        database = db
        return db
    }

    /**
     * Exact whole-word / whole-phrase search (NOT a substring match).
     * Searching "log" will match the word "log" but not "dialogue" or
     * "catalog". Multi-word queries match that exact word sequence,
     * e.g. "the Lord is my shepherd" only matches verses containing
     * that phrase, not verses with those words scattered separately.
     */
    fun search(query: String): List<Verse> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()
        val db = open()
        val results = mutableListOf<Verse>()

        // Escape any quotes the user typed, then wrap the whole query
        // in quotes so FTS treats it as one exact phrase/word rather
        // than "any of these words anywhere".
        val escaped = trimmed.replace("\"", "\"\"")
        val ftsQuery = "\"$escaped\""

        val cursor = db.rawQuery(
            "SELECT book, chapter, verse, text FROM verses " +
                "WHERE verses MATCH ? ORDER BY rowid LIMIT 300",
            arrayOf(ftsQuery)
        )
        cursor.use {
            while (it.moveToNext()) {
                results.add(
                    Verse(
                        book = it.getString(0),
                        chapter = it.getInt(1),
                        verse = it.getInt(2),
                        text = it.getString(3)
                    )
                )
            }
        }
        return results
    }

    fun close() {
        database?.close()
        database = null
    }
}
