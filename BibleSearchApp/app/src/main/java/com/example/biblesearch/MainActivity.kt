package com.example.biblesearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper
    private lateinit var adapter: VerseAdapter
    private lateinit var emptyView: TextView
    private lateinit var searchBox: EditText
    private lateinit var suggestionsContainer: LinearLayout
    private var currentTestament: DbHelper.Testament = DbHelper.Testament.ALL
    private var isApplyingSuggestion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DbHelper(applicationContext)

        searchBox = findViewById(R.id.searchBox)
        val recyclerView: RecyclerView = findViewById(R.id.resultsRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        suggestionsContainer = findViewById(R.id.suggestionsContainer)
        val testamentFilter: RadioGroup = findViewById(R.id.testamentFilter)

        adapter = VerseAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        testamentFilter.setOnCheckedChangeListener { _, checkedId ->
            currentTestament = when (checkedId) {
                R.id.filterOT -> DbHelper.Testament.OLD
                R.id.filterNT -> DbHelper.Testament.NEW
                else -> DbHelper.Testament.ALL
            }
            runSearch(searchBox.text.toString())
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isApplyingSuggestion) return
                val text = s?.toString().orEmpty()
                runSearch(text)
                updateSuggestions(text)
            }
        })
    }

    private fun runSearch(query: String) {
        val results = dbHelper.search(query.trim(), currentTestament)
        adapter.updateData(results)
        emptyView.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
        emptyView.text = if (query.isBlank()) "Type a word or phrase to search the KJV Bible."
                          else "No verses found for \"$query\"."
    }

    /** Shows tappable word suggestions based on the last word being typed. */
    private fun updateSuggestions(fullText: String) {
        suggestionsContainer.removeAllViews()
        val lastWord = fullText.trim().substringAfterLast(" ")
        if (lastWord.length < 2) return

        val suggestions = dbHelper.suggestWords(lastWord)
            .filter { !it.equals(lastWord, ignoreCase = true) }

        for (word in suggestions) {
            val chip = TextView(this).apply {
                text = word
                setPadding(28, 16, 28, 16)
                setBackgroundResource(android.R.drawable.edit_text)
                gravity = Gravity.CENTER
                textSize = 14f
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.marginEnd = 16
                layoutParams = params
                setOnClickListener { applySuggestion(word) }
            }
            suggestionsContainer.addView(chip)
        }
    }

    private fun applySuggestion(word: String) {
        isApplyingSuggestion = true
        val current = searchBox.text.toString()
        val newText = if (current.contains(" ")) {
            current.substringBeforeLast(" ") + " " + word
        } else {
            word
        }
        searchBox.setText(newText)
        searchBox.setSelection(newText.length)
        isApplyingSuggestion = false
        runSearch(newText)
        suggestionsContainer.removeAllViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}
