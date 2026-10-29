package com.example.worldexplorer.model.network

import com.example.worldexplorer.model.data.CountriesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestCountriesService {

    @GET("countries/v5")
    suspend fun getCountries(
        @Query("region") region: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("response_fields") responseFields: String? = null
    ): Response<CountriesResponse>

    @GET("countries/v5/codes.alpha_2/{code}")
    suspend fun getCountryByCode(
        @Path("code") code: String
    ): Response<CountriesResponse>
}