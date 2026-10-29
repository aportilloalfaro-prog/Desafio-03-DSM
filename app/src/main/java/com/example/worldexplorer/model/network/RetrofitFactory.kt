package com.example.worldexplorer.model.network

import com.example.worldexplorer.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {

    private const val COUNTRIES_URL = "https://api.restcountries.com/"
    private const val GEONAMES_URL = "https://secure.geonames.org/"
    private const val WEATHER_URL = "https://api.weatherapi.com/v1/"

    // Bearer token en el encabezado
    private val bearerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.RESTCOUNTRIES_TOKEN}")
            .build()
        chain.proceed(request)
    }

    // Agrega un parámetro fijo (username / key) a cada solicitud
    private fun queryInterceptor(name: String, value: String) = Interceptor { chain ->
        val original = chain.request()
        val url = original.url.newBuilder().addQueryParameter(name, value).build()
        chain.proceed(original.newBuilder().url(url).build())
    }

    private fun buildClient(interceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

    private fun buildRetrofit(baseUrl: String, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val countriesService: RestCountriesService by lazy {
        buildRetrofit(COUNTRIES_URL, buildClient(bearerInterceptor))
            .create(RestCountriesService::class.java)
    }

    val geoNamesService: GeoNamesService by lazy {
        val client = buildClient(queryInterceptor("username", BuildConfig.GEONAMES_USERNAME))
        buildRetrofit(GEONAMES_URL, client).create(GeoNamesService::class.java)
    }

    val weatherService: WeatherService by lazy {
        val client = buildClient(queryInterceptor("key", BuildConfig.WEATHERAPI_KEY))
        buildRetrofit(WEATHER_URL, client).create(WeatherService::class.java)
    }
}