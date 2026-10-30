package com.luisortiz.notasapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider

class CreateNoteActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        useLayout(R.layout.activity_create_note)
        val model = ViewModelProvider(this)[CreateNoteViewModel::class.java]
        val title = findViewById<EditText>(R.id.title)
        val content = findViewById<EditText>(R.id.content)
        val save = findViewById<Button>(R.id.save)
        val message = findViewById<TextView>(R.id.message)
        val progress = findViewById<ProgressBar>(R.id.progress)
        findViewById<Button>(R.id.back).setOnClickListener { finish() }
        save.setOnClickListener { model.save(title.text.toString(), content.text.toString()) }
        model.state.observe(this) { state ->
            progress.visibility = if (state.loading) View.VISIBLE else View.GONE
            save.isEnabled = !state.loading
            title.isEnabled = !state.loading
            content.isEnabled = !state.loading
            message.text = state.error ?: ""
            state.data?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                model.reset()
                finish()
            }
        }
    }
}
