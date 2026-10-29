package com.example.worldexplorer.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldexplorer.controller.CountriesController
import com.example.worldexplorer.databinding.ActivityCountriesBinding
import com.example.worldexplorer.model.data.Country
import com.example.worldexplorer.util.ApiResult
import com.example.worldexplorer.view.adapter.CountriesAdapter
import kotlinx.coroutines.launch

class CountriesActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REGION_API = "extra_region_api"
        const val EXTRA_REGION_NAME = "extra_region_name"
    }

    private lateinit var binding: ActivityCountriesBinding
    private val controller = CountriesController()
    private val adapter = CountriesAdapter { country -> openDetail(country) }
    private var regionApi: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        regionApi = intent.getStringExtra(EXTRA_REGION_API).orEmpty()
        val regionName = intent.getStringExtra(EXTRA_REGION_NAME).orEmpty()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = regionName

        binding.rvCountries.layoutManager = LinearLayoutManager(this)
        binding.rvCountries.adapter = adapter
        binding.btnRetry.setOnClickListener { loadCountries() }

        loadCountries()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadCountries() {
        showLoading()
        lifecycleScope.launch {
            when (val result = controller.getCountriesByRegion(regionApi)) {
                is ApiResult.Success -> showList(result.data)
                is ApiResult.Failure -> showError(result.message)
            }
        }
    }

    private fun showLoading() {
        binding.progress.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
        binding.rvCountries.visibility = View.GONE
    }

    private fun showList(countries: List<Country>) {
        binding.progress.visibility = View.GONE
        binding.layoutError.visibility = View.GONE
        binding.rvCountries.visibility = View.VISIBLE
        adapter.submit(countries)
    }

    private fun showError(message: String) {
        binding.progress.visibility = View.GONE
        binding.rvCountries.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvError.text = message
    }

    private fun openDetail(country: Country) {
        val code = country.codes?.alpha2
        if (code.isNullOrBlank()) {
            Toast.makeText(this, "Este país no tiene código disponible.", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, CountryDetailActivity::class.java).apply {
            putExtra(CountryDetailActivity.EXTRA_CODE, code)
            putExtra(CountryDetailActivity.EXTRA_NAME, country.displayName)
        }
        startActivity(intent)
    }
}