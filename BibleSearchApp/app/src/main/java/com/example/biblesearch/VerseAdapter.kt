package com.example.biblesearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VerseAdapter(private var verses: List<Verse>) :
    RecyclerView.Adapter<VerseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reference: TextView = view.findViewById(R.id.referenceText)
        val text: TextView = view.findViewById(R.id.verseText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_verse, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val verse = verses[position]
        holder.reference.text = verse.reference
        holder.text.text = verse.text
    }

    override fun getItemCount(): Int = verses.size

    fun updateData(newVerses: List<Verse>) {
        verses = newVerses
        notifyDataSetChanged()
    }
}
