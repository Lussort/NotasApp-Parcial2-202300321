package com.luisortiz.notasapp

// Modelos: objetos que se envían y reciben como JSON.
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val name: String)
data class NoteRequest(val title: String, val content: String)
data class MessageResponse(val message: String)
data class Note(val id: Int, val title: String, val content: String, val created_at: String)
// Cada pantalla observa un estado: carga, resultado o error.
data class UiState<T>(val loading: Boolean = false, val data: T? = null, val error: String? = null)
