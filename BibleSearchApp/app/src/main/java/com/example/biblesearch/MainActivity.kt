package com.example.biblesearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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
    private var currentTestament: DbHelper.Testament = DbHelper.Testament.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DbHelper(applicationContext)

        searchBox = findViewById(R.id.searchBox)
        val recyclerView: RecyclerView = findViewById(R.id.resultsRecyclerView)
        emptyView = findViewById(R.id.emptyView)
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
                runSearch(s?.toString().orEmpty())
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

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}
