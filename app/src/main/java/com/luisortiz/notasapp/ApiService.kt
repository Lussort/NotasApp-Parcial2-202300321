package com.luisortiz.notasapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("login") fun login(@Body body: LoginRequest): Call<LoginResponse>
    @POST("notes") fun createNote(@Body body: NoteRequest): Call<MessageResponse>
    @GET("notes") fun getNotes(): Call<List<Note>>
}
