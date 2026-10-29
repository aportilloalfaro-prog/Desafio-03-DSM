package com.example.worldexplorer.model.data

data class CityWeather(
    val city: City,
    val isCapital: Boolean = false,
    val weather: CurrentWeather? = null,
    val weatherError: String? = null,
    val loading: Boolean = true
)