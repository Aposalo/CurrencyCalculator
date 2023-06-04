package aposalo.com.currencycalculator.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import aposalo.com.currencycalculator.adapters.CountriesAdapter
import aposalo.com.currencycalculator.databinding.ActivityCountryListBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CountryModel
import aposalo.com.currencycalculator.domain.model.CountrySymbols
import aposalo.com.currencycalculator.util.StateManager
import aposalo.com.currencycalculator.util.Constants
import aposalo.com.currencycalculator.util.Resource

class ActivityCountryList : AppCompatActivity() {

    private lateinit var binding: ActivityCountryListBinding
    private var mDb: AppDatabase? = null
    private lateinit var viewModel: CountryModel
    private lateinit var countriesAdapter : CountriesAdapter
    private lateinit var layout: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryListBinding.inflate(layoutInflater)
        mDb = AppDatabase.getInstance(applicationContext)
        setContentView(binding.root)
        setupRecyclerView()
        viewModel = CountryModel(mDb)
        viewModel.countriesRepository.data.observe(this) { response ->
            when(response){
                is Resource.Success -> {
                    response.data?.let { countries ->
                        val currencyList : ArrayList<CountrySymbols> = ArrayList()
                        countries.currencies.forEach { (k, v) ->
                            val countrySymbol = CountrySymbols(k,v)
                            currencyList.add(countrySymbol)
                        }
                        countriesAdapter.differ.submitList(currencyList)
                    }
                }
                is Resource.Error -> {
                    Log.e(TAG, "Countries cannot be loaded.")
                }
                is Resource.Loading -> {
                    Log.d(TAG, "Countries are loaded.")
                }
            }
        }
        viewModel.getCountries()

        binding.searchCountries.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.getCountries(s.toString())
            }
        })
    }

    private fun adapterOnClick(countrySymbol: CountrySymbols) {
        if (intent != null && intent.hasExtra(Constants.CURRENCY_CHANGE)) {
            layout = intent.getStringExtra(Constants.CURRENCY_CHANGE) ?: ""
        }
        val stateManager = StateManager(resources, this)
        stateManager.updateCountryValue(layout, countrySymbol.symbol)
        finish()
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