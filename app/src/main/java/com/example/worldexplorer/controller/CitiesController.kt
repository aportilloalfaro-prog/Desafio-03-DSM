package com.example.worldexplorer.controller

import com.example.worldexplorer.model.data.City
import com.example.worldexplorer.model.data.GeoNamesStatus
import com.example.worldexplorer.model.network.GeoNamesService
import com.example.worldexplorer.model.network.RetrofitFactory
import com.example.worldexplorer.util.ApiResult
import com.example.worldexplorer.util.ErrorType
import com.example.worldexplorer.util.safeApiCall
import java.util.concurrent.ConcurrentHashMap

class CitiesController(
    private val service: GeoNamesService = RetrofitFactory.geoNamesService
) {

    companion object {
        private val cache = ConcurrentHashMap<String, List<City>>()
    }

    suspend fun getTopCities(countryCode: String): ApiResult<List<City>> {
        cache[countryCode]?.let { return ApiResult.Success(it) }

        val result = safeApiCall { service.getTopCities(countryCode) }
        when (result) {
            is ApiResult.Failure -> return result
            is ApiResult.Success -> {
                val body = result.data
                body.status?.let { return mapStatus(it) }

                val cities = body.geonames.orEmpty().filter {
                    !it.name.isNullOrBlank() && it.lat != null && it.lng != null
                }
                if (cities.isEmpty()) {
                    return ApiResult.Failure(
                        ErrorType.NOT_FOUND,
                        "Este país no tiene ciudades principales disponibles."
                    )
                }
                cache[countryCode] = cities
                return ApiResult.Success(cities)
            }
        }
    }

    private fun mapStatus(status: GeoNamesStatus): ApiResult.Failure =
        when (status.value) {
            10, 11 -> ApiResult.Failure(
                ErrorType.INVALID_CREDENTIALS,
                "Usuario de GeoNames inválido o sin servicios web habilitados."
            )
            18, 19, 20 -> ApiResult.Failure(
                ErrorType.RATE_LIMIT,
                "Se alcanzó el límite de solicitudes de GeoNames."
            )
            else -> ApiResult.Failure(
                ErrorType.UNKNOWN,
                status.message ?: "Error al consultar las ciudades."
            )
        }
}