package com.luisortiz.notasapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListNotesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        useLayout(R.layout.activity_list_notes)
        val model = ViewModelProvider(this)[ListNotesViewModel::class.java]
        val adapter = NotesAdapter()
        findViewById<RecyclerView>(R.id.notes).apply {
            layoutManager = LinearLayoutManager(this@ListNotesActivity)
            this.adapter = adapter
        }
        val refresh = findViewById<Button>(R.id.refresh)
        val progress = findViewById<ProgressBar>(R.id.progress)
        val message = findViewById<TextView>(R.id.message)
        findViewById<Button>(R.id.back).setOnClickListener { finish() }
        refresh.setOnClickListener { model.load() }
        model.state.observe(this) { state ->
            progress.visibility = if (state.loading) View.VISIBLE else View.GONE
            refresh.isEnabled = !state.loading
            message.text = state.error ?: if (state.data?.isEmpty() == true) "Todavía no tienes notas. Crea la primera desde el inicio." else ""
            state.data?.let { adapter.update(it) }
        }
        if (model.state.value?.data == null) model.load()
    }
}
