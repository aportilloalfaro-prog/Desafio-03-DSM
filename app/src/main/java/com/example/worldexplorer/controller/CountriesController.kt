package com.example.worldexplorer.controller

import com.example.worldexplorer.model.data.Country
import com.example.worldexplorer.model.network.RestCountriesService
import com.example.worldexplorer.model.network.RetrofitFactory
import com.example.worldexplorer.util.ApiResult
import com.example.worldexplorer.util.ErrorType
import com.example.worldexplorer.util.safeApiCall
import java.util.concurrent.ConcurrentHashMap

class CountriesController(
    private val service: RestCountriesService = RetrofitFactory.countriesService
) {

    companion object {
        private const val PAGE_SIZE = 100
        // Solo lo necesario para la lista: nombre, código, capital y bandera
        private const val LIST_FIELDS = "names,codes,capitals,flag.url_png,region"

        // Memoria compartida: cuida la cuota mensual de la cuenta gratis
        private val regionCache = ConcurrentHashMap<String, List<Country>>()
        private val detailCache = ConcurrentHashMap<String, Country>()
    }

    suspend fun getCountriesByRegion(region: String): ApiResult<List<Country>> {
        regionCache[region]?.let { return ApiResult.Success(it) }

        val all = mutableListOf<Country>()
        var offset = 0
        while (true) {
            val result = safeApiCall {
                service.getCountries(
                    region = region,
                    limit = PAGE_SIZE,
                    offset = offset,
                    responseFields = LIST_FIELDS
                )
            }
            when (result) {
                is ApiResult.Failure -> return result
                is ApiResult.Success -> {
                    val data = result.data.data
                    all += data?.objects.orEmpty()
                    if (data?.meta?.more != true) break
                    offset += PAGE_SIZE
                }
            }
        }

        if (all.isEmpty()) {
            return ApiResult.Failure(ErrorType.NOT_FOUND, "No se encontraron países para esta región.")
        }

        val sorted = all.sortedBy { it.displayName }
        regionCache[region] = sorted
        return ApiResult.Success(sorted)
    }

    // Trae el país completo (todos los campos) para la pantalla de detalle
    suspend fun getCountryByCode(code: String): ApiResult<Country> {
        detailCache[code]?.let { return ApiResult.Success(it) }

        return when (val result = safeApiCall { service.getCountryByCode(code) }) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> {
                val country = result.data.data?.objects?.firstOrNull()
                if (country == null) {
                    ApiResult.Failure(ErrorType.NOT_FOUND, "No se encontró información de este país.")
                } else {
                    detailCache[code] = country
                    ApiResult.Success(country)
                }
            }
        }
    }
}