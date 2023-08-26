package aposalo.com.currencycalculator.domain.model

import android.annotation.SuppressLint
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
import aposalo.com.currencycalculator.utils.CalculationExtensions.getSolution
import aposalo.com.currencycalculator.utils.Resource
import aposalo.com.currencycalculator.utils.isOnline
import io.sentry.Sentry
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
//https://stackoverflow.com/questions/63942547/databinding-a-viewmodel-should-not-have-a-reference-to-the-views-but
@SuppressLint("StaticFieldLeak")
class CurrencyCalculatorModel(
    private var binding: ActivityMainBinding,
    private var resources: Resources,
    private val mDb: AppDatabase?,
    private val context: Context) : ViewModel() {

    private val currencyCalculatorRepository: CurrencyCalculatorRepository = CurrencyCalculatorRepository(mDb)

    private val tag = CurrencyCalculatorModel::class.java.simpleName

    init {
        viewModelScope.launch {
            currencyCalculatorRepository.dataCurrencyCalculator.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        val msg = response.message
                        binding.currencyTv.text =
                            if (msg != resources.getString(R.string.zero) && isOnline(context)) msg?.getSolution() ?: resources.getString(R.string.zero)
                            else calculateCurrencyByRate()
                    }
                    is Resource.Error -> {
                        response.message?.let { message ->
                            Log.e(tag, "An error occurred: $message")
                            Sentry.captureMessage("An error occurred: $message")
                        }
                    }
                    else -> {
                        binding.currencyTv.text = resources.getString(R.string.loader)
                    }
                }
            }
        }
    }

    private suspend fun calculateCurrencyByRate():String {
        return try {
            val res = binding.resultTv.text.toString().toFloat()
            val resRate = mDb?.latestRateDao()?.getResult(
                to = binding.currencyText.text.toString(),
                from = binding.resultText.text.toString()
            )
            if (resRate != null) {
                val rate = resRate.rate
                val curr = rate.times(res)
                curr.toString().getSolution()
            } else {
                Toast.makeText(
                    context,
                    "There is no rate, please connect to internet.",
                    Toast.LENGTH_SHORT
                ).show()
                Sentry.captureMessage("There is no rate, please connect to internet.")
            }
            binding.currencyTv.text.toString()
        }
        catch (e: Exception) {
            val msg = e.message.toString()
            Log.e(tag, "calculateCurrencyByRate: $msg",e)
            "Err"
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
