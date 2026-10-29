package com.example.worldexplorer.controller

import com.example.worldexplorer.model.data.CurrentWeather
import com.example.worldexplorer.model.network.RetrofitFactory
import com.example.worldexplorer.model.network.WeatherService
import com.example.worldexplorer.util.ApiResult
import com.example.worldexplorer.util.ErrorType
import com.example.worldexplorer.util.safeApiCall

class WeatherController(
    private val service: WeatherService = RetrofitFactory.weatherService
) {

    suspend fun getCurrentWeather(lat: Double, lng: Double): ApiResult<CurrentWeather> {
        return when (val result = safeApiCall { service.getCurrentWeather("$lat,$lng") }) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> {
                val current = result.data.current
                if (current == null) {
                    ApiResult.Failure(ErrorType.NOT_FOUND, "Clima no disponible para esta ubicación.")
                } else {
                    ApiResult.Success(current)
                }
            }
        }
    }
}