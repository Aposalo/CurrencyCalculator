package aposalo.com.currencycalculator.domain.model

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aposalo.com.currencycalculator.R
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.repository.CurrencyCalculatorRepository
import aposalo.com.currencycalculator.util.Extensions.Companion.getSolution
import aposalo.com.currencycalculator.util.Resource
import aposalo.com.currencycalculator.util.TAG
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CurrencyCalculatorModel(
    private var binding: ActivityMainBinding,
    private var resources: Resources,
    mDb: AppDatabase?) : ViewModel() {

    private val currencyCalculatorRepository: CurrencyCalculatorRepository = CurrencyCalculatorRepository(mDb)

    init {
        viewModelScope.launch {
            currencyCalculatorRepository.data.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        binding.currencyTv.text = response.message?.getSolution() ?: resources.getString(R.string.init_value)
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            binding.currencyTv.text = "err"
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
            currencyCalculatorRepository.getFixerConvert(to, from, amount)
        }
    }

}
