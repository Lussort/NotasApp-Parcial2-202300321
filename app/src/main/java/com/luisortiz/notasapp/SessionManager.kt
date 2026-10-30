package com.luisortiz.notasapp

import android.content.Context

class SessionManager(context: Context) {
    private val preferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token: String? get() = preferences.getString("token", null)
    val name: String get() = preferences.getString("name", "Usuario") ?: "Usuario"
    fun save(response: LoginResponse) {
        preferences.edit().putString("token", response.token).putString("name", response.name).apply()
    }
    fun clear() { preferences.edit().clear().apply() }
}
