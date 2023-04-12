package aposalo.com.currencycalculator.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CurrencyCalculatorModel
import aposalo.com.currencycalculator.listeners.CalculatorListener
import aposalo.com.currencycalculator.util.ActivityMainStateManager
import aposalo.com.currencycalculator.util.Constants.Companion.CURRENCY_CHANGE
import aposalo.com.currencycalculator.util.Constants.Companion.CURRENCY_VALUE
import aposalo.com.currencycalculator.util.GoogleManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.launch


const val TAG = "MainActivity"
const val SHARED_PREF = "currency_calculator"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrencyCalculatorModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var stateManager: ActivityMainStateManager

    private var mDb: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val reviewManager = ReviewManagerFactory.create(applicationContext)
        GoogleManager.requestReviewInfo(reviewManager, this)
        mDb = AppDatabase.getInstance(applicationContext)
        viewModel = CurrencyCalculatorModel(binding, resources, mDb)
        stateManager = ActivityMainStateManager(binding, resources, this)

        if (intent != null && intent.hasExtra(CURRENCY_CHANGE) && intent.hasExtra(CURRENCY_VALUE)) {
            val currencyChange = intent.getStringExtra(CURRENCY_CHANGE) ?: ""
            val currencyValue = intent.getStringExtra(CURRENCY_VALUE) ?: ""
            if (currencyChange == "result"){
                stateManager.updateResultValue(currencyValue)
            }
            else if (currencyChange == "currency"){
                stateManager.updateCurrencyValue(currencyValue)
            }
            stateManager.restoreLastState()
            binding.resultTv.text.toString().updateCurrency()
        }
        else{
            stateManager.restoreLastState()
        }

        binding.resultTv.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                s.toString().updateCurrency()
            }
        })
        binding.resultLayout.setOnClickListener(onClickCountryChange("result"))
        binding.currencyLayout.setOnClickListener(onClickCountryChange("currency"))

        val buttonListener = CalculatorListener(binding, resources)
        binding.x.setOnClickListener(buttonListener)
        binding.openBracket.setOnClickListener(buttonListener)
        binding.closeBracket.setOnClickListener(buttonListener)
        binding.divide.setOnClickListener(buttonListener)
        binding.multiply.setOnClickListener(buttonListener)
        binding.plus.setOnClickListener(buttonListener)
        binding.minus.setOnClickListener(buttonListener)
        binding.equals.setOnClickListener(buttonListener)
        binding.zero.setOnClickListener(buttonListener)
        binding.one.setOnClickListener(buttonListener)
        binding.two.setOnClickListener(buttonListener)
        binding.three.setOnClickListener(buttonListener)
        binding.four.setOnClickListener(buttonListener)
        binding.five.setOnClickListener(buttonListener)
        binding.six.setOnClickListener(buttonListener)
        binding.seven.setOnClickListener(buttonListener)
        binding.eight.setOnClickListener(buttonListener)
        binding.nine.setOnClickListener(buttonListener)
        binding.c.setOnClickListener(buttonListener)
        binding.dot.setOnClickListener(buttonListener)
    }

    private fun onClickCountryChange(layout: String): View.OnClickListener {
        return View.OnClickListener {
            stateManager.saveLastState()
            val intent = Intent(this@MainActivity, ActivityCountryList::class.java)
            intent.putExtra(CURRENCY_CHANGE, layout)
            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        stateManager.saveLastState()
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onStop() {
        stateManager.saveLastState()
        lifecycleScope.launch {
            mDb?.currencyCalculatorDao()?.clearDatabase()
        }
        super.onStop()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        stateManager.restoreLastState()
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onDestroy() {
        stateManager.saveLastState()
        super.onDestroy()
    }

    private fun String.updateCurrency() {
        val toSelectedItem = binding.currencyButton.text.toString()
        val fromSelectedItem = binding.resultText.text.toString()
        val floatResult = this.toFloatOrNull() ?: 0.0f
        viewModel.getUserPage(toSelectedItem, fromSelectedItem, floatResult)
    }
}