package com.example.worldexplorer.controller

import com.example.worldexplorer.model.data.Region

class RegionsController {

    // Las regiones de REST Countries son fijas, no hace falta gastar una solicitud
    fun getRegions(): List<Region> = listOf(
        Region("Africa", "África"),
        Region("Americas", "América"),
        Region("Asia", "Asia"),
        Region("Europe", "Europa"),
        Region("Oceania", "Oceanía")
    )
}