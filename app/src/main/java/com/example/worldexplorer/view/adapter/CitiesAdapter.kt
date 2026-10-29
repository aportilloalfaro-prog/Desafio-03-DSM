package com.example.worldexplorer.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.worldexplorer.databinding.ItemCityBinding
import com.example.worldexplorer.model.data.CityWeather
import com.example.worldexplorer.util.toSummary
import com.google.android.material.color.MaterialColors
import java.text.NumberFormat

class CitiesAdapter(
    private val onCityClick: (CityWeather) -> Unit
) : RecyclerView.Adapter<CitiesAdapter.CityViewHolder>() {

    private val items = mutableListOf<CityWeather>()

    fun submit(list: List<CityWeather>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    // Se llama cuando llega el clima de UNA ciudad: solo se refresca esa tarjeta
    fun update(item: CityWeather) {
        val index = items.indexOfFirst {
            it.city.geonameId == item.city.geonameId && it.city.name == item.city.name
        }
        if (index >= 0) {
            items[index] = item
            notifyItemChanged(index)
        }
    }

    inner class CityViewHolder(val binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding
        val density = b.root.resources.displayMetrics.density

        b.tvCityName.text = if (item.isCapital) "★ ${item.city.name} (capital)" else item.city.name
        b.tvPopulation.text = "Población: " +
                (item.city.population?.let { NumberFormat.getIntegerInstance().format(it) } ?: "-")

        // La capital se destaca con un borde del color principal
        b.root.strokeWidth = if (item.isCapital) (3 * density).toInt() else 0
        b.root.strokeColor = MaterialColors.getColor(b.root, androidx.appcompat.R.attr.colorPrimary)

        b.progressWeather.visibility = View.GONE
        b.ivWeather.visibility = View.GONE
        b.tvWeather.visibility = View.GONE
        b.tvWeatherExtra.visibility = View.GONE
        b.tvWeatherError.visibility = View.GONE

        val weather = item.weather
        when {
            item.loading -> b.progressWeather.visibility = View.VISIBLE
            weather != null -> {
                b.ivWeather.visibility = View.VISIBLE
                b.ivWeather.load(weather.condition?.iconUrl)
                b.tvWeather.visibility = View.VISIBLE
                b.tvWeather.text = weather.toSummary()
                b.tvWeatherExtra.visibility = View.VISIBLE
                b.tvWeatherExtra.text =
                    "Viento: ${weather.windKph ?: "-"} kph (${weather.windMph ?: "-"} mph) · " +
                            "Humedad: ${weather.humidity ?: "-"}%\n" +
                            "Actualizado: ${weather.lastUpdated ?: "-"}"
            }
            else -> {
                b.tvWeatherError.visibility = View.VISIBLE
                b.tvWeatherError.text = "Clima no disponible: ${item.weatherError ?: "error desconocido"}"
            }
        }

        b.root.setOnClickListener { onCityClick(item) }
    }

    override fun getItemCount(): Int = items.size
}