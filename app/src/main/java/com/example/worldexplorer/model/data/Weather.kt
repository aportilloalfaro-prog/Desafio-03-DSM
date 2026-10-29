package com.example.worldexplorer.model.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val location: WeatherLocation? = null,
    val current: CurrentWeather? = null,
    val error: WeatherError? = null
)

data class WeatherLocation(
    val name: String? = null,
    val region: String? = null,
    val country: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val localtime: String? = null
)

data class CurrentWeather(
    @SerializedName("last_updated") val lastUpdated: String? = null,
    @SerializedName("temp_c") val tempC: Double? = null,
    @SerializedName("temp_f") val tempF: Double? = null,
    val condition: WeatherCondition? = null,
    @SerializedName("wind_kph") val windKph: Double? = null,
    @SerializedName("wind_mph") val windMph: Double? = null,
    val humidity: Int? = null
)

data class WeatherCondition(
    val text: String? = null,
    val icon: String? = null,
    val code: Int? = null
) {
    // WeatherAPI devuelve el ícono como "//cdn.weatherapi.com/..." (sin https)
    val iconUrl: String?
        get() = icon?.let { if (it.startsWith("//")) "https:$it" else it }
}

data class WeatherError(
    val code: Int? = null,
    val message: String? = null
)