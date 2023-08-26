package aposalo.com.currencycalculator.activities

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import aposalo.com.currencycalculator.R
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CurrencyCalculatorModel
import aposalo.com.currencycalculator.listeners.CalculatorListenerSetter
import aposalo.com.currencycalculator.utils.CURRENCY_TEXT_LABEL
import aposalo.com.currencycalculator.utils.CalculationExtensions.getSolution
import aposalo.com.currencycalculator.utils.CurrencyCalculatorTextWatcher
import aposalo.com.currencycalculator.utils.RESULT_TEXT_LABEL
import aposalo.com.currencycalculator.utils.Resource
import aposalo.com.currencycalculator.utils.isOnline
import io.sentry.Sentry
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : MainActivityExtension() {

    private val tag = MainActivity::class.java.simpleName

    private lateinit var viewModel: CurrencyCalculatorModel

    private var mDb: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = AppDatabase.getInstance(applicationContext)
        viewModel = CurrencyCalculatorModel(mDb = mDb)
        binding.resultTv.addTextChangedListener(resultTextWatcher)
        binding.resultLayout.setOnClickListener(onClickCountryChange(RESULT_TEXT_LABEL))
        binding.currencyLayout.setOnClickListener(onClickCountryChange(CURRENCY_TEXT_LABEL))
        CalculatorListenerSetter(binding, resources)
        lifecycleScope.launch {
            viewModel.dataCurrencyCalculator.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        val msg = response.message
                        binding.currencyTv.text =
                            if (msg != resources.getString(R.string.zero) && isOnline(this@MainActivity)) msg?.getSolution() ?: resources.getString(
                                R.string.zero)
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
                    this,
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

    private val resultTextWatcher = object : CurrencyCalculatorTextWatcher {
        override fun afterTextChanged(s: Editable) {
            s.toString().updateCurrency()
        }
    }


    private fun String.updateCurrency() {
        viewModel.getUserPage(
            from = binding.resultText.text.toString(),
            to = binding.currencyText.text.toString(),
            amount = toFloatOrNull() ?: 0.0f
        )
    }
}
