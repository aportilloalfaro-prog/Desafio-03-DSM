package com.example.worldexplorer

import android.app.Application
import org.osmdroid.config.Configuration

class WorldApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // OpenStreetMap exige un user agent propio que identifique la app.
        // Con el nombre del paquete (com.example...) bloquea las descargas.
        Configuration.getInstance().userAgentValue =
            "MundoExplorer/1.0 (desafio academico Universidad Don Bosco)"
    }
}