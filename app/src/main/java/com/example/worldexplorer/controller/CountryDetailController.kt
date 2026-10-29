package com.example.worldexplorer.controller

import com.example.worldexplorer.model.data.CityWeather
import com.example.worldexplorer.model.data.Country
import com.example.worldexplorer.util.ApiResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CountryDetailController(
    private val countries: CountriesController = CountriesController(),
    private val cities: CitiesController = CitiesController(),
    private val weather: WeatherController = WeatherController()
) {

    suspend fun loadCountry(code: String): ApiResult<Country> =
        countries.getCountryByCode(code)

    // Ciudades con la capital marcada y puesta al inicio
    suspend fun loadCities(code: String, capitalName: String?): ApiResult<List<CityWeather>> {
        return when (val result = cities.getTopCities(code)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> {
                val items = result.data.map { city ->
                    val isCapital = city.isCapital ||
                            (capitalName != null && city.name.equals(capitalName, ignoreCase = true))
                    CityWeather(city = city, isCapital = isCapital)
                }
                // sortedByDescending es estable: la capital sube y el resto conserva el orden por población
                ApiResult.Success(items.sortedByDescending { it.isCapital })
            }
        }
    }

    // Clima de todas las ciudades en paralelo; onResult se llama cuando llega cada una
    suspend fun loadWeather(items: List<CityWeather>, onResult: (CityWeather) -> Unit) {
        coroutineScope {
            items.forEach { item ->
                launch {
                    val lat = item.city.lat
                    val lng = item.city.lng
                    val updated = if (lat == null || lng == null) {
                        item.copy(loading = false, weatherError = "Ubicación no encontrada.")
                    } else {
                        when (val result = weather.getCurrentWeather(lat, lng)) {
                            is ApiResult.Success -> item.copy(weather = result.data, loading = false)
                            is ApiResult.Failure -> item.copy(weatherError = result.message, loading = false)
                        }
                    }
                    onResult(updated)
                }
            }
        }
    }
}