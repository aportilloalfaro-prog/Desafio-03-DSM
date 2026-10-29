package com.example.worldexplorer.model.data

import com.google.gson.annotations.SerializedName

data class CountriesResponse(
    val data: CountriesData? = null
)

data class CountriesData(
    val objects: List<Country>? = null,
    val meta: ApiMeta? = null
)

data class ApiMeta(
    val total: Int? = null,
    val count: Int? = null,
    val limit: Int? = null,
    val offset: Int? = null,
    val more: Boolean? = null
)

data class Country(
    val uuid: String? = null,
    val names: CountryNames? = null,
    val codes: CountryCodes? = null,
    val capitals: List<Capital>? = null,
    val flag: Flag? = null,
    val region: String? = null,
    val subregion: String? = null,
    val area: Area? = null,
    val borders: List<String>? = null,
    @SerializedName("calling_codes") val callingCodes: List<String>? = null,
    val continents: List<String>? = null,
    val coordinates: Coordinates? = null,
    val currencies: List<Currency>? = null,
    val languages: List<Language>? = null,
    val population: Long? = null,
    val timezones: List<String>? = null,
    val tlds: List<String>? = null,
    val landlocked: Boolean? = null,
    @SerializedName("government_type") val governmentType: String? = null
) {
    // Nombre para mostrar: español si existe, si no el común en inglés
    val displayName: String
        get() = names?.translations?.get("spa")?.common ?: names?.common ?: "Sin nombre"

    val displayOfficialName: String
        get() = names?.translations?.get("spa")?.official ?: names?.official ?: "-"

    val capitalName: String
        get() = capitals?.firstOrNull()?.name ?: "Sin capital"

    val flagUrl: String?
        get() = flag?.urlPng
}

data class CountryNames(
    val common: String? = null,
    val official: String? = null,
    val translations: Map<String, NameTranslation>? = null
)

data class NameTranslation(
    val common: String? = null,
    val official: String? = null
)

data class CountryCodes(
    @SerializedName("alpha_2") val alpha2: String? = null,
    @SerializedName("alpha_3") val alpha3: String? = null,
    val ccn3: String? = null
)

data class Capital(
    val name: String? = null,
    val coordinates: Coordinates? = null
)

data class Coordinates(
    val lat: Double? = null,
    val lng: Double? = null
)

data class Flag(
    val emoji: String? = null,
    val description: String? = null,
    @SerializedName("url_png") val urlPng: String? = null,
    @SerializedName("url_svg") val urlSvg: String? = null
)

data class Area(
    val kilometers: Double? = null,
    val miles: Double? = null
)

data class Currency(
    val code: String? = null,
    val name: String? = null,
    val symbol: String? = null
)

data class Language(
    val name: String? = null,
    @SerializedName("native_name") val nativeName: String? = null,
    val iso639_1: String? = null
)