package com.luisortiz.notasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DashboardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        useLayout(R.layout.activity_dashboard)
        val session = SessionManager(this)
        findViewById<TextView>(R.id.welcome).text = "Bienvenido, ${session.name}"
        findViewById<Button>(R.id.create).setOnClickListener { startActivity(Intent(this, CreateNoteActivity::class.java)) }
        findViewById<Button>(R.id.list).setOnClickListener { startActivity(Intent(this, ListNotesActivity::class.java)) }
        findViewById<Button>(R.id.logout).setOnClickListener {
            session.clear()
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}
