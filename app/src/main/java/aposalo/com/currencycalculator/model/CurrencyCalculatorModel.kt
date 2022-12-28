package aposalo.com.currencycalculator.model

import android.content.res.Resources
import aposalo.com.currencycalculator.R
import aposalo.com.currencycalculator.TAG
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.repository.CurrencyCalculatorRepository
import aposalo.com.currencycalculator.util.Extensions.Companion.toTwoDecimalsString
import aposalo.com.currencycalculator.util.Resource
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CurrencyCalculatorModel(
    private var binding: ActivityMainBinding,
    private var resources: Resources) : ViewModel() {

    private val repository: CurrencyCalculatorRepository = CurrencyCalculatorRepository()

    init {
        viewModelScope.launch {
            repository.data.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.currencyTv.text = response.data?.result?.toString()?.toTwoDecimalsString() ?: resources.getString(R.string.init_value)
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.e(TAG, "An error occurred: $message")
                        }
                    }
                    is Resource.Loading -> {
                        binding.currencyTv.text = resources.getString(R.string.loader)
                    }
                }
            }
        }
    }


    fun getUserPage(to: String, from: String, amount: Float) {
        viewModelScope.launch {
            repository.getFixerConvert(to, from, amount)
        }
    }
}