package com.example.biblesearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper
    private lateinit var adapter: VerseAdapter
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DbHelper(applicationContext)

        val searchBox: EditText = findViewById(R.id.searchBox)
        val recyclerView: RecyclerView = findViewById(R.id.resultsRecyclerView)
        emptyView = findViewById(R.id.emptyView)

        adapter = VerseAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                runSearch(s?.toString().orEmpty())
            }
        })
    }

    private fun runSearch(query: String) {
        val results = dbHelper.search(query.trim())
        adapter.updateData(results)
        emptyView.visibility = if (results.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        emptyView.text = if (query.isBlank()) "Type a word or phrase to search the KJV Bible."
                          else "No verses found for \"$query\"."
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}
