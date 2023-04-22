package aposalo.com.currencycalculator.domain.model

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aposalo.com.currencycalculator.R
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.repository.CurrencyCalculatorRepository
import aposalo.com.currencycalculator.util.Extensions.Companion.getDefaultCalculation
import aposalo.com.currencycalculator.util.Extensions.Companion.getSolution
import aposalo.com.currencycalculator.util.InternetConnectivity
import aposalo.com.currencycalculator.util.Resource
import aposalo.com.currencycalculator.util.TAG
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CurrencyCalculatorModel(
    private var binding: ActivityMainBinding,
    private var resources: Resources,
    mDb: AppDatabase?,
    context: Context) : ViewModel() {

    private val currencyCalculatorRepository: CurrencyCalculatorRepository = CurrencyCalculatorRepository(mDb)

    private var rate : Float = 0.88f

    init {
        viewModelScope.launch {
            currencyCalculatorRepository.dataCurrencyCalculator.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        if (InternetConnectivity.isOnline(context)){
                            binding.currencyTv.text = response.message?.getSolution() ?: resources.getString(R.string.init_value)
                        }
                        else {
                            var res = binding.solutionTv.text.toString().getDefaultCalculation().toFloatOrNull()
                            val toSelectedItem = binding.currencyText.text.toString()
                            val fromSelectedItem = binding.resultText.text.toString()
                            val resRate = mDb?.latestRateDao()?.getResult(to = toSelectedItem, from = fromSelectedItem)
                            if (resRate != null) {
                                rate = resRate.getRate()
                            }
                            res = res ?: 0.0f
                            val curr = rate.times(res)
                            binding.currencyTv.text = curr.toString().getSolution()
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            var res = binding.solutionTv.text.toString().getDefaultCalculation().toFloatOrNull()
                            val toSelectedItem = binding.currencyText.text.toString()
                            val fromSelectedItem = binding.resultText.text.toString()
                            val resRate = mDb?.latestRateDao()?.getResult(to = toSelectedItem, from = fromSelectedItem)
                            if (resRate != null) {
                                rate = resRate.getRate()
                            }
                            res = res ?: 0.0f
                            val curr = rate.times(res!!)
                            binding.currencyTv.text = curr.toString().getSolution()
                            Log.e(TAG, "An error occurred: $message")
                        }
                    }
                    is Resource.Loading -> {
                        binding.currencyTv.text = resources.getString(R.string.loader)
                    }
                }
            }
        }

        viewModelScope.launch {
            currencyCalculatorRepository.dataLatestRate.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        if (InternetConnectivity.isOnline(context)){
                            response.message?.let { responseRate ->
                                rate = responseRate.toFloat()
                            }
                        }
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.e(TAG, "An error occurred: $message")
                        }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun getUserPage(to: String, from: String, amount: Float) {
        viewModelScope.launch {
            currencyCalculatorRepository.getCurrencyValue(to, from, amount)
        }
    }

    fun getLatestRate(to: String, from: String) {
        viewModelScope.launch {
            currencyCalculatorRepository.getLatestRateValue(to, from)
        }
    }

}
