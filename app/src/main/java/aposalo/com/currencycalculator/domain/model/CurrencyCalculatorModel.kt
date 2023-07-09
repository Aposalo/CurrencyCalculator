package aposalo.com.currencycalculator.domain.model

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aposalo.com.currencycalculator.R
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.repository.CurrencyCalculatorRepository
import aposalo.com.currencycalculator.util.CalculationExtensions.Companion.getDefaultCalculation
import aposalo.com.currencycalculator.util.CalculationExtensions.Companion.getSolution
import aposalo.com.currencycalculator.util.InternetConnectivity
import aposalo.com.currencycalculator.util.Resource
import aposalo.com.currencycalculator.util.TAG
import io.sentry.Sentry
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CurrencyCalculatorModel(
    private var binding: ActivityMainBinding,
    private var resources: Resources,
    private val mDb: AppDatabase?,
    private val context: Context) : ViewModel() {

    private val currencyCalculatorRepository: CurrencyCalculatorRepository = CurrencyCalculatorRepository(mDb)

    init {
        viewModelScope.launch {
            currencyCalculatorRepository.dataCurrencyCalculator.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        val msg = response.message
                        if (msg != "0" && InternetConnectivity.isOnline(context)) binding.currencyTv.text = msg?.getSolution() ?: resources.getString(R.string.init_value)
                        else calculateCurrencyOffline()
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            calculateCurrencyOffline()
                            Log.e(TAG, "An error occurred: $message")
                            Sentry.captureMessage(message)
                        }
                    }
                    is Resource.Loading -> {
                        binding.currencyTv.text = resources.getString(R.string.loader)
                    }
                }
            }
        }
    }

    private suspend fun calculateCurrencyOffline() {
        val res = binding.resultTv.text.toString().toFloat()
        val resRate = mDb?.latestRateDao()?.getResult(
            to = binding.currencyText.text.toString(),
            from = binding.resultText.text.toString()
        )

        if (resRate != null) {
            val rate = resRate.getRate()
            val curr = rate.times(res)
            binding.currencyTv.text = curr.toString().getSolution()
        }
        else {
            Toast.makeText(context,
                "There is no rate, please connect to internet.",
                Toast.LENGTH_SHORT).show()
            Sentry.captureMessage("There is no rate, please connect to internet.")
        }
    }

    fun getUserPage(from: String, to: String,  amount: Float) {
        viewModelScope.launch {
            currencyCalculatorRepository.getCurrencyValue (
                from = from,
                to = to,
                amount = amount
            )
        }
    }

}
