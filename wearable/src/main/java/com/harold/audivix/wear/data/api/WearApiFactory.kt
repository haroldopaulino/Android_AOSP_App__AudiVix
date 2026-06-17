package com.harold.audivix.wear.data.api

import com.harold.audivix.wear.data.model.AUDIVIX_WEAR_ENDPOINT
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object WearApiFactory {
    fun create(endpoint: String = AUDIVIX_WEAR_ENDPOINT): WearAudiVixApi {
        val baseUrl = if (endpoint.endsWith("/")) endpoint else "$endpoint/"
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WearAudiVixApi::class.java)
    }
}
