package com.example.worldexplorer.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldexplorer.controller.RegionsController
import com.example.worldexplorer.databinding.ActivityRegionsBinding
import com.example.worldexplorer.view.adapter.RegionsAdapter

class RegionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegionsBinding
    private val controller = RegionsController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.rvRegions.layoutManager = LinearLayoutManager(this)
        binding.rvRegions.adapter = RegionsAdapter(controller.getRegions()) { region ->
            val intent = Intent(this, CountriesActivity::class.java).apply {
                putExtra(CountriesActivity.EXTRA_REGION_API, region.apiName)
                putExtra(CountriesActivity.EXTRA_REGION_NAME, region.displayName)
            }
            startActivity(intent)
        }
    }
}