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
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.launch

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrencyCalculatorModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var stateManager: ActivityMainStateManager

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
        viewModel = CurrencyCalculatorModel(binding, resources, mDb)
        stateManager = ActivityMainStateManager(resources, this)
        stateManager.setBinding(binding)
        stateManager.restoreLastState()
        binding.resultTv.text.toString().updateCurrency()

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
        buttonListener.setOnClickListenerButtons()
    }

    override fun onRestart() {
        stateManager.restoreLastState()
        hasMovedToCountries = false
        super.onRestart()
    }

    private fun onClickCountryChange(layout: String): View.OnClickListener {
        return View.OnClickListener {
            stateManager.saveLastState()
            val intent = Intent(this@MainActivity, ActivityCountryList::class.java)
            intent.putExtra(CURRENCY_CHANGE, layout)
            hasMovedToCountries = true
            startActivity(intent)
        }
    }

    override fun onStop() {
        stateManager.saveLastState()
        if (!hasMovedToCountries){
            lifecycleScope.launch {
                mDb?.currencyCalculatorDao()?.clearDatabase()
            }
        }
        super.onStop()
    }

    private fun String.updateCurrency() {
        val toSelectedItem = binding.currencyButton.text.toString()
        val fromSelectedItem = binding.resultText.text.toString()
        val floatResult = this.toFloatOrNull() ?: 0.0f
        viewModel.getUserPage(toSelectedItem, fromSelectedItem, floatResult)
    }
}