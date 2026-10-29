package com.example.worldexplorer.model.network

import com.example.worldexplorer.model.data.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    // El parámetro "key" lo agrega automáticamente RetrofitFactory
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("q") latLon: String,
        @Query("lang") lang: String = "es"
    ): Response<WeatherResponse>
}