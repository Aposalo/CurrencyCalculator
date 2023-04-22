package aposalo.com.currencycalculator.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CurrencyCalculatorModel
import aposalo.com.currencycalculator.listeners.CalculatorListener
import aposalo.com.currencycalculator.util.Constants
import aposalo.com.currencycalculator.util.Constants.Companion.CURRENCY_TEXT_LABEL
import aposalo.com.currencycalculator.util.Constants.Companion.RESULT_TEXT_LABEL
import aposalo.com.currencycalculator.util.InternetConnectivity
import aposalo.com.currencycalculator.util.StateManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.launch

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrencyCalculatorModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var stateManager: StateManager

    private var mDb: AppDatabase? = null

    private var hasMovedToCountries: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val reviewManager = ReviewManagerFactory.create(applicationContext)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewManager.launchReviewFlow(this, task.result)
            }
        }

        mDb = AppDatabase.getInstance(applicationContext)
        viewModel = CurrencyCalculatorModel(binding, resources, mDb, this)
        stateManager = StateManager(resources, this)
        stateManager.setBinding(binding)
        binding.resultTv.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                s.toString().updateCurrency()
            }
        })
        binding.resultLayout.setOnClickListener(onClickCountryChange(RESULT_TEXT_LABEL))
        binding.currencyLayout.setOnClickListener(onClickCountryChange(CURRENCY_TEXT_LABEL))
        stateManager.restoreLastState()

        val buttonListener = CalculatorListener(binding, resources)
        buttonListener.setOnClickListenerButtons()
        updateRate()

    }

    override fun onRestart() {
        stateManager.restoreLastState()
        hasMovedToCountries = false
        updateRate()
        super.onRestart()
    }

    private fun onClickCountryChange (layout: String) : View.OnClickListener {
        return View.OnClickListener {
            if (InternetConnectivity.isOnline(this)){
                stateManager.saveLastState()
                val intent = Intent(this@MainActivity, ActivityCountryList::class.java)
                intent.putExtra(Constants.CURRENCY_CHANGE, layout)
                hasMovedToCountries = true
                startActivity(intent)
            }
            else {
                Toast.makeText(this,"No Internet Access",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        stateManager.saveLastState()
        if (!hasMovedToCountries) {
            lifecycleScope.launch {
                mDb?.currencyCalculatorDao()?.clearCurrencies()
                mDb?.countryDao()?.clearCountry()
            }
        }
        super.onStop()
    }

    private fun updateRate(){
        if (InternetConnectivity.isOnline(this)) {
            val toSelectedItem = binding.currencyText.text.toString()
            val fromSelectedItem = binding.resultText.text.toString()
            viewModel.getLatestRate(toSelectedItem, fromSelectedItem)
        }
    }

    private fun String.updateCurrency() {
        val toSelectedItem = binding.currencyText.text.toString()
        val fromSelectedItem = binding.resultText.text.toString()
        val floatResult = this.toFloatOrNull() ?: 0.0f
        viewModel.getUserPage(to = toSelectedItem, from = fromSelectedItem, amount = floatResult)
    }
}
