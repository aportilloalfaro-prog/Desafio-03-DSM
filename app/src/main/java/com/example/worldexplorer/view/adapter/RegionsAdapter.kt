package com.example.worldexplorer.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.worldexplorer.databinding.ItemRegionBinding
import com.example.worldexplorer.model.data.Region

class RegionsAdapter(
    private val regions: List<Region>,
    private val onRegionClick: (Region) -> Unit
) : RecyclerView.Adapter<RegionsAdapter.RegionViewHolder>() {

    inner class RegionViewHolder(val binding: ItemRegionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val binding = ItemRegionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val region = regions[position]
        holder.binding.tvRegionName.text = region.displayName
        holder.binding.root.setOnClickListener { onRegionClick(region) }
    }

    override fun getItemCount(): Int = regions.size
}