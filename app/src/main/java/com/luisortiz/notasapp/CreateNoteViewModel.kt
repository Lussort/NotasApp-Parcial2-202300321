package com.luisortiz.notasapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call

class CreateNoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotesRepository(application)
    private val mutableState = MutableLiveData(UiState<MessageResponse>())
    val state: LiveData<UiState<MessageResponse>> = mutableState
    private var request: Call<*>? = null
    fun save(title: String, content: String) {
        if (mutableState.value?.loading == true) return
        if (title.isBlank() || content.isBlank()) {
            mutableState.value = UiState(error = "Completa el título y el contenido."); return
        }
        mutableState.value = UiState(loading = true)
        request = repository.create(title.trim(), content.trim()) { mutableState.value = it }
    }
    fun reset() { mutableState.value = UiState() }
    override fun onCleared() { request?.cancel() }
}
