package com.luisortiz.notasapp

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // 10.0.2.2 es la dirección del PC vista desde el emulador Android.
    private const val BASE_URL = "http://127.0.0.1:5000/api/"
    fun create(context: Context): ApiService {
        val session = SessionManager(context.applicationContext)
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                session.token?.let { request.header("Authorization", "Bearer $it") }
                chain.proceed(request.build())
            }.build()
        return Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }
}
