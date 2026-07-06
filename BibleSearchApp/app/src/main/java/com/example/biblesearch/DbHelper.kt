package com.example.biblesearch

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.FileOutputStream

class DbHelper(private val context: Context) {

    private val dbName = "kjv_v3.db"
    private var database: SQLiteDatabase? = null

    enum class Testament { ALL, OLD, NEW }

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

    /** Exact whole-word / whole-phrase search, optionally filtered by testament. */
    fun search(query: String, testament: Testament): List<Verse> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()
        val db = open()
        val results = mutableListOf<Verse>()

        val escaped = trimmed.replace("\"", "\"\"")
        val ftsQuery = "\"$escaped\""

        val sql = when (testament) {
            Testament.ALL -> "SELECT book, chapter, verse, text FROM verses " +
                "WHERE verses MATCH ? ORDER BY rowid LIMIT 300"
            Testament.OLD -> "SELECT book, chapter, verse, text FROM verses " +
                "WHERE verses MATCH ? AND testament = 'OT' ORDER BY rowid LIMIT 300"
            Testament.NEW -> "SELECT book, chapter, verse, text FROM verses " +
                "WHERE verses MATCH ? AND testament = 'NT' ORDER BY rowid LIMIT 300"
        }

        val cursor = db.rawQuery(sql, arrayOf(ftsQuery))
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

    /** Tap-to-complete word suggestions based on the word currently being typed. */
    fun suggestWords(prefix: String, limit: Int = 8): List<String> {
        val cleaned = prefix.trim().lowercase()
        if (cleaned.length < 2) return emptyList()
        val db = open()
        val results = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT word FROM words WHERE word LIKE ? ORDER BY freq DESC LIMIT ?",
            arrayOf("$cleaned%", limit.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                results.add(it.getString(0))
            }
        }
        return results
    }

    fun close() {
        database?.close()
        database = null
    }
}
