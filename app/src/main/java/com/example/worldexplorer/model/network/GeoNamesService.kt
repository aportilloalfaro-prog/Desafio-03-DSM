package com.example.worldexplorer.model.network

import com.example.worldexplorer.model.data.GeoNamesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesService {

    // El parámetro "username" lo agrega automáticamente RetrofitFactory
    @GET("searchJSON")
    suspend fun getTopCities(
        @Query("country") countryCode: String,
        @Query("featureClass") featureClass: String = "P",
        @Query("cities") cities: String = "cities15000",
        @Query("maxRows") maxRows: Int = 10,
        @Query("orderby") orderBy: String = "population"
    ): Response<GeoNamesResponse>
}