package com.example.worldexplorer.util

import com.example.worldexplorer.model.data.CurrentWeather

fun CurrentWeather.toSummary(): String {
    val c = tempC?.let { "$it°C" } ?: "-"
    val f = tempF?.let { "$it°F" } ?: "-"
    val cond = condition?.text.orEmpty()
    return "$c / $f · $cond"
}