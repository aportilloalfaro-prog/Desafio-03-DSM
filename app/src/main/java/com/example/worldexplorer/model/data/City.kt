package com.example.worldexplorer.model.data

data class GeoNamesResponse(
    val geonames: List<City>? = null,
    val totalResultsCount: Int? = null,
    // GeoNames responde 200 con este bloque cuando hay un error (usuario inválido, límite, etc.)
    val status: GeoNamesStatus? = null
)

data class GeoNamesStatus(
    val message: String? = null,
    val value: Int? = null
)

data class City(
    val geonameId: Long? = null,
    val name: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val population: Long? = null,
    val fcode: String? = null,
    val countryCode: String? = null,
    val adminName1: String? = null
) {
    // PPLC = capital de un país en GeoNames
    val isCapital: Boolean
        get() = fcode == "PPLC"
}