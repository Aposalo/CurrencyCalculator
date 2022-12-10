package android.example.com.currencycalculator.model

import android.content.res.Resources
import android.example.com.currencycalculator.R
import android.example.com.currencycalculator.TAG
import android.example.com.currencycalculator.databinding.ActivityMainBinding
import android.example.com.currencycalculator.repository.CurrencyCalculatorRepository
import android.example.com.currencycalculator.util.Extensions.Companion.toTwoDecimalsString
import android.example.com.currencycalculator.util.Resource
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CurrencyCalculatorModel(var binding: ActivityMainBinding, var resources: Resources) : ViewModel() {

    private val repository: CurrencyCalculatorRepository = CurrencyCalculatorRepository()

    init {
        viewModelScope.launch {
            repository.data.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.currencyTv.text = response.data?.result?.toString()?.toTwoDecimalsString()
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