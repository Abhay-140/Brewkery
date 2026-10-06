package com.abhay.brewkery.data.api

import com.abhay.brewkery.data.model.BrewkeryResponse
import com.abhay.brewkery.data.model.MenuItem
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface BrewkeryApiService {

    @GET("data.json")
    suspend fun getMenu(): BrewkeryResponse

    @GET("api/items/{id}.json")
    suspend fun getItemDetail(@Path("id") id: Int): MenuItem
}

object RetrofitClient {
    private const val BASE_URL = "https://raw.githubusercontent.com/VivekShah138/Brewkery/main/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: BrewkeryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BrewkeryApiService::class.java)
    }
}
