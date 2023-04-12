package aposalo.com.currencycalculator.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import aposalo.com.currencycalculator.adapters.CountriesAdapter
import aposalo.com.currencycalculator.databinding.ActivityCountryListBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CountryModel
import aposalo.com.currencycalculator.domain.model.CountrySymbols
import aposalo.com.currencycalculator.util.Constants.Companion.CURRENCY_CHANGE
import aposalo.com.currencycalculator.util.Constants.Companion.CURRENCY_VALUE
import aposalo.com.currencycalculator.util.Resource

class ActivityCountryList : AppCompatActivity() {

    private lateinit var binding: ActivityCountryListBinding
    private var mDb: AppDatabase? = null
    private lateinit var viewModel: CountryModel
    private lateinit var countriesAdapter : CountriesAdapter
    private var currencyList : ArrayList<CountrySymbols> = ArrayList()
    private lateinit var layout: String;

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null && intent.hasExtra(CURRENCY_CHANGE)) {
            layout = intent.getStringExtra(CURRENCY_CHANGE) ?: "";
        }
        binding = ActivityCountryListBinding.inflate(layoutInflater)
        mDb = AppDatabase.getInstance(applicationContext)
        setContentView(binding.root)
        setupRecyclerView()
        viewModel = CountryModel()
        viewModel.countriesRepository.data.observe(this) { response ->
            when(response){
                is Resource.Success -> {
                    response.data?.let { countries ->
                        currencyList = ArrayList()
                        countries.symbols.forEach { (k, v) ->
                            val countrySymbol = CountrySymbols(k,v)
                            currencyList.add(countrySymbol)
                        }
                        countriesAdapter.differ.submitList(currencyList)
                        hideProgressBar()
                    }
                }
                is Resource.Error -> {
                    Log.e(TAG, "Countries cannot be loaded")
                    hideProgressBar()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getCountries()
    }

    private fun adapterOnClick(countrySymbol: CountrySymbols) {
        val intent = Intent(this, MainActivity()::class.java)
        intent.putExtra(CURRENCY_CHANGE, layout);
        intent.putExtra(CURRENCY_VALUE, countrySymbol.symbol);
        this.startActivity(intent)
    }

    private fun setupRecyclerView() {
        countriesAdapter = CountriesAdapter { countrySymbol ->
                        adapterOnClick(countrySymbol)
                    }
        binding.rvUCountries.apply{
            adapter = countriesAdapter
            layoutManager = LinearLayoutManager(this@ActivityCountryList)
        }

    }

}