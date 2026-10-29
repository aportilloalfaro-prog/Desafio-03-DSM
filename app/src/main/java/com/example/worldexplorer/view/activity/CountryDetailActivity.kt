package com.example.worldexplorer.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.worldexplorer.controller.CountryDetailController
import com.example.worldexplorer.databinding.ActivityCountryDetailBinding
import com.example.worldexplorer.model.data.City
import com.example.worldexplorer.model.data.CityWeather
import com.example.worldexplorer.model.data.Country
import com.example.worldexplorer.util.ApiResult
import com.example.worldexplorer.util.toSummary
import com.example.worldexplorer.view.adapter.CitiesAdapter
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import java.text.NumberFormat

class CountryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CODE = "extra_country_code"
        const val EXTRA_NAME = "extra_country_name"
    }

    private lateinit var binding: ActivityCountryDetailBinding
    private val controller = CountryDetailController()
    private val adapter = CitiesAdapter { item -> focusCity(item) }
    private val markers = mutableMapOf<Long, Marker>()
    private var countryCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        countryCode = intent.getStringExtra(EXTRA_CODE).orEmpty()
        val name = intent.getStringExtra(EXTRA_NAME).orEmpty()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = name

        setupMap()
        binding.rvCities.layoutManager = LinearLayoutManager(this)
        binding.rvCities.adapter = adapter
        binding.btnRetry.setOnClickListener { load() }

        load()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        binding.map.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.map.onDetach()
        super.onDestroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupMap() {
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)
        binding.map.controller.setZoom(4.0)

        // El mapa vive dentro de un scroll: sin esto el dedo desplazaría la pantalla y no el mapa
        binding.map.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->
                    v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    // 1) País  2) Ciudades  3) Clima de cada ciudad (en paralelo)
    private fun load() {
        showLoading()
        lifecycleScope.launch {
            when (val result = controller.loadCountry(countryCode)) {
                is ApiResult.Failure -> showError(result.message)
                is ApiResult.Success -> {
                    showContent()
                    bindCountry(result.data)
                    loadCities(result.data)
                }
            }
        }
    }

    private suspend fun loadCities(country: Country) {
        binding.progressCities.visibility = View.VISIBLE
        binding.tvCitiesMessage.visibility = View.GONE
        binding.map.overlays.clear()
        markers.clear()
        adapter.submit(emptyList())

        when (val result = controller.loadCities(countryCode, country.capitalName)) {
            is ApiResult.Failure -> {
                // Un fallo aquí no tumba la pantalla: se muestra el mensaje y el resto sigue visible
                binding.progressCities.visibility = View.GONE
                binding.tvCitiesMessage.visibility = View.VISIBLE
                binding.tvCitiesMessage.text = result.message
            }
            is ApiResult.Success -> {
                val items = result.data
                binding.progressCities.visibility = View.GONE
                adapter.submit(items)
                addMarkers(items)
                fitMap(items)
                // Cada clima se pinta apenas llega; si uno falla, solo esa ciudad muestra el error
                controller.loadWeather(items) { updated ->
                    adapter.update(updated)
                    updateMarker(updated)
                }
            }
        }
    }

    private fun bindCountry(country: Country) {
        binding.ivFlag.load(country.flagUrl) { crossfade(true) }
        binding.tvCommonName.text = country.displayName
        binding.tvOfficialName.text = country.displayOfficialName

        val population = country.population
            ?.let { NumberFormat.getIntegerInstance().format(it) } ?: "-"
        val area = country.area?.kilometers
            ?.let { NumberFormat.getIntegerInstance().format(it) } ?: "-"
        val currencies = country.currencies.orEmpty()
            .joinToString { "${it.name} (${it.code} ${it.symbol})" }
            .ifBlank { "-" }
        val languages = country.languages.orEmpty()
            .joinToString { it.name ?: "-" }
            .ifBlank { "-" }

        binding.tvDetails.text = buildString {
            appendLine("Capital: ${country.capitalName}")
            appendLine("Región: ${country.region ?: "-"}")
            appendLine("Subregión: ${country.subregion ?: "-"}")
            appendLine("Población: $population")
            appendLine("Área: $area km²")
            appendLine(
                "Códigos ISO: ${country.codes?.alpha2 ?: "-"} / " +
                        "${country.codes?.alpha3 ?: "-"} / ${country.codes?.ccn3 ?: "-"}"
            )
            appendLine("Monedas: $currencies")
            appendLine("Idiomas: $languages")
            append(
                "Coordenadas: ${country.coordinates?.lat ?: "-"}, ${country.coordinates?.lng ?: "-"}"
            )
        }

        // Mapa centrado en el país mientras llegan las ciudades
        val lat = country.coordinates?.lat
        val lng = country.coordinates?.lng
        if (lat != null && lng != null) {
            binding.map.controller.setCenter(GeoPoint(lat, lng))
        }
    }

    private fun cityKey(city: City): Long = city.geonameId ?: city.name.hashCode().toLong()

    private fun addMarkers(items: List<CityWeather>) {
        items.forEach { item ->
            val lat = item.city.lat ?: return@forEach
            val lng = item.city.lng ?: return@forEach
            val marker = Marker(binding.map).apply {
                position = GeoPoint(lat, lng)
                title = if (item.isCapital) "★ ${item.city.name}" else item.city.name
                snippet = "Cargando clima..."
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            binding.map.overlays.add(marker)
            markers[cityKey(item.city)] = marker
        }
        binding.map.invalidate()
    }

    private fun updateMarker(item: CityWeather) {
        val marker = markers[cityKey(item.city)] ?: return
        val weather = item.weather
        marker.snippet = weather?.toSummary() ?: "Clima no disponible"
        if (marker.isInfoWindowShown) marker.showInfoWindow()
        binding.map.invalidate()
    }

    // Encuadra todas las ciudades en el mapa
    private fun fitMap(items: List<CityWeather>) {
        val points = items.mapNotNull {
            val lat = it.city.lat
            val lng = it.city.lng
            if (lat != null && lng != null) GeoPoint(lat, lng) else null
        }
        binding.map.post {
            when {
                points.size > 1 ->
                    binding.map.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), true, 100)
                points.size == 1 -> {
                    binding.map.controller.setZoom(9.0)
                    binding.map.controller.setCenter(points[0])
                }
            }
        }
    }

    // Al tocar una ciudad de la lista: sube al mapa, lo centra y abre su globo con nombre y clima
    private fun focusCity(item: CityWeather) {
        val lat = item.city.lat ?: return
        val lng = item.city.lng ?: return
        binding.scroll.smoothScrollTo(0, binding.map.top)
        binding.map.controller.animateTo(GeoPoint(lat, lng), 11.0, 800L)
        InfoWindow.closeAllInfoWindowsOn(binding.map)
        markers[cityKey(item.city)]?.showInfoWindow()
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.scroll.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
    }

    private fun showContent() {
        binding.progress.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        binding.scroll.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.progress.visibility = View.GONE
        binding.scroll.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvError.text = message
    }
}