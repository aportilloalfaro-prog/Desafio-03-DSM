package com.example.worldexplorer.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.worldexplorer.R
import com.example.worldexplorer.databinding.ItemCountryBinding
import com.example.worldexplorer.model.data.Country

class CountriesAdapter(
    private val onCountryClick: (Country) -> Unit
) : RecyclerView.Adapter<CountriesAdapter.CountryViewHolder>() {

    private var countries: List<Country> = emptyList()

    fun submit(newList: List<Country>) {
        countries = newList
        notifyDataSetChanged()
    }

    inner class CountryViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]
        val b = holder.binding
        val context = b.root.context

        b.tvCountryName.text = country.displayName
        b.tvCapital.text = context.getString(R.string.capital_label, country.capitalName)
        b.ivFlag.load(country.flagUrl) { crossfade(true) }
        b.root.setOnClickListener { onCountryClick(country) }
    }

    override fun getItemCount(): Int = countries.size
}