package com.luisortiz.notasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteHolder>() {
    private var notes: List<Note> = emptyList()
    fun update(items: List<Note>) { notes = items; notifyDataSetChanged() }
    class NoteHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.noteTitle)
        val content: TextView = view.findViewById(R.id.noteContent)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false))
    override fun getItemCount() = notes.size
    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.title.text = notes[position].title
        holder.content.text = notes[position].content
    }
}
