package com.luisortiz.notasapp

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

// Evita que los controles queden debajo de las barras de Android 15/16.
open class BaseActivity : AppCompatActivity() {
    protected fun useLayout(layout: Int) {
        setContentView(layout)
        val root = findViewById<android.view.View>(R.id.main)
        val padding = (20 * resources.displayMetrics.density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.updatePadding(left = padding + bars.left, top = padding + bars.top,
                right = padding + bars.right, bottom = padding + bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(root)
    }
}
