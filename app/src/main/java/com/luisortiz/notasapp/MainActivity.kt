package com.luisortiz.notasapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        useLayout(R.layout.activity_main)
        val model = ViewModelProvider(this)[LoginViewModel::class.java]
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val button = findViewById<Button>(R.id.login)
        val progress = findViewById<ProgressBar>(R.id.progress)
        val message = findViewById<TextView>(R.id.message)
        button.setOnClickListener { model.login(email.text.toString(), password.text.toString()) }
        model.state.observe(this) { state ->
            progress.visibility = if (state.loading) View.VISIBLE else View.GONE
            button.isEnabled = !state.loading
            message.text = state.error ?: ""
            if (state.data != null) {
                model.reset()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        }
    }
}
