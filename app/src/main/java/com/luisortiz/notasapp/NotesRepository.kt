package com.luisortiz.notasapp

import android.content.Context
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// El Repository es el único lugar que ejecuta las llamadas de la API.
class NotesRepository(context: Context) {
    private val api = ApiClient.create(context)
    private val session = SessionManager(context)
    fun login(email: String, password: String, done: (UiState<LoginResponse>) -> Unit): Call<LoginResponse> {
        return execute(api.login(LoginRequest(email, password))) { state ->
            state.data?.let { session.save(it) }
            done(state)
        }
    }
    fun create(title: String, content: String, done: (UiState<MessageResponse>) -> Unit) =
        execute(api.createNote(NoteRequest(title, content)), done)
    fun list(done: (UiState<List<Note>>) -> Unit) = execute(api.getNotes(), done)

    private fun <T> execute(call: Call<T>, done: (UiState<T>) -> Unit): Call<T> {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    done(UiState(data = body))
                } else {
                    val message = try {
                        JSONObject(response.errorBody()?.string() ?: "{}").optString("message", "Error del servidor.")
                    } catch (e: Exception) { "Respuesta no válida del servidor." }
                    done(UiState(error = message))
                }
            }
            override fun onFailure(call: Call<T>, error: Throwable) {
                if (!call.isCanceled) done(UiState(error = "No se pudo conectar. Comprueba que el backend esté encendido."))
            }
        })
        return call
    }
}
