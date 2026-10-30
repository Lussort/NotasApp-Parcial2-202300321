package com.luisortiz.notasapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotesRepository(application)
    private val mutableState = MutableLiveData(UiState<LoginResponse>())
    val state: LiveData<UiState<LoginResponse>> = mutableState
    private var request: Call<*>? = null
    fun login(email: String, password: String) {
        if (mutableState.value?.loading == true) return
        if (email.isBlank() || password.isBlank()) {
            mutableState.value = UiState(error = "Ingresa correo y contraseña."); return
        }
        mutableState.value = UiState(loading = true)
        request = repository.login(email.trim(), password) { mutableState.value = it }
    }
    fun reset() { mutableState.value = UiState() }
    override fun onCleared() { request?.cancel() }
}
