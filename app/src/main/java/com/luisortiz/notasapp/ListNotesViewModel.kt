package com.luisortiz.notasapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call

class ListNotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotesRepository(application)
    private val mutableState = MutableLiveData(UiState<List<Note>>())
    val state: LiveData<UiState<List<Note>>> = mutableState
    private var request: Call<*>? = null
    fun load() {
        if (mutableState.value?.loading == true) return
        mutableState.value = UiState(loading = true)
        request = repository.list { mutableState.value = it }
    }
    fun reset() { mutableState.value = UiState() }
    override fun onCleared() { request?.cancel() }
}
