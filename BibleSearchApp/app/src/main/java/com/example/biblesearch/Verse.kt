package com.example.biblesearch

data class Verse(
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String
) {
    val reference: String
        get() = "$book $chapter:$verse"
}
