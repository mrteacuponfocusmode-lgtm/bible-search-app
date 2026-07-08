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

    /**
     * Prefix search: typing "belie" finds "believed", "believeth", etc.
     * Each typed word gets a trailing * (SQLite FTS prefix wildcard),
     * so you don't have to finish typing a word to get matches.
     */
    fun search(query: String, testament: Testament): List<Verse> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()
        val db = open()
        val results = mutableListOf<Verse>()

        val words = trimmed
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .map { it.replace("\"", "").replace("*", "") + "*" }

        if (words.isEmpty()) return emptyList()
        val ftsQuery = words.joinToString(" ")

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

    fun close() {
        database?.close()
        database = null
    }
}
