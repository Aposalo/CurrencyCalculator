package aposalo.com.currencycalculator.activities

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import aposalo.com.currencycalculator.adapters.CountriesAdapter
import aposalo.com.currencycalculator.databinding.ActivityCountryListBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CountryModel
import aposalo.com.currencycalculator.domain.model.CountrySymbols
import aposalo.com.currencycalculator.utils.CURRENCY_CHANGE
import aposalo.com.currencycalculator.utils.CurrencyCalculatorTextWatcher
import aposalo.com.currencycalculator.utils.Resource
import aposalo.com.currencycalculator.utils.StateManager
import io.sentry.Sentry

class ActivityCountryList : AppCompatActivity() {

    private lateinit var binding: ActivityCountryListBinding
    private var mDb: AppDatabase? = null
    private lateinit var viewModel: CountryModel
    private lateinit var countriesAdapter : CountriesAdapter
    private lateinit var layout: String

    private val tag = ActivityCountryList::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryListBinding.inflate(layoutInflater)
        mDb = AppDatabase.getInstance(applicationContext)
        setContentView(binding.root)
        setupRecyclerView()
        viewModel = CountryModel(mDb)
        viewModel.countriesRepository.data.observe(this) { response ->
            when(response) {
                is Resource.Success -> {
                    stopProgressBar()
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
                    stopProgressBar()
                    Log.e(tag, response.message!!)
                    Sentry.captureMessage(response.message)
                }
                else -> {
                    startProgressBar()
                    Log.d(tag, "Countries are loaded.")
                }
            }
        }
        viewModel.getCountries()
        binding.searchCountries.addTextChangedListener(countriesTextWatcher)
    }

    private fun stopProgressBar() {
        if (binding.progressBar.visibility == View.VISIBLE) {
            binding.progressBar.visibility = View.GONE
            binding.rvUCountriesLinearLayout.visibility = View.VISIBLE
        }
    }

    private fun startProgressBar() {
        if (binding.progressBar.visibility == View.GONE) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvUCountriesLinearLayout.visibility = View.GONE
        }
    }

    private val countriesTextWatcher = object : CurrencyCalculatorTextWatcher {
        override fun afterTextChanged(s: Editable) {
            viewModel.getCountries(s.toString())
        }
    }

    private fun adapterOnClick(countrySymbol: CountrySymbols) {
        layout = if (intent != null && intent.hasExtra(CURRENCY_CHANGE)) {
            intent.getStringExtra(CURRENCY_CHANGE) ?: String()
        }
        else {
            String()
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