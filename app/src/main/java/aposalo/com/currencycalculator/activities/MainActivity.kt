package aposalo.com.currencycalculator.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CurrencyCalculatorModel
import aposalo.com.currencycalculator.listeners.CalculatorListener
import aposalo.com.currencycalculator.utils.Constants
import aposalo.com.currencycalculator.utils.Constants.Companion.CURRENCY_TEXT_LABEL
import aposalo.com.currencycalculator.utils.Constants.Companion.RESULT_TEXT_LABEL
import aposalo.com.currencycalculator.utils.InternetConnectivity
import aposalo.com.currencycalculator.utils.StateManager
import aposalo.com.currencycalculator.workers.cleanDatabase.CleanDatabaseWorkRequest
import aposalo.com.currencycalculator.workers.rateWorker.RateWorkerOneTimeWorkRequest
import aposalo.com.currencycalculator.workers.rateWorker.RateWorkerPeriodicWorkRequest
import com.google.android.play.core.review.ReviewManagerFactory

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrencyCalculatorModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var stateManager: StateManager

    private lateinit var rateWorkerWorkRequest: RateWorkerPeriodicWorkRequest

    private var mDb: AppDatabase? = null

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
        stateManager = StateManager(resources, this, binding)
        val cleanDatabaseWorkRequest = CleanDatabaseWorkRequest(this)
        cleanDatabaseWorkRequest.startWork()
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
        rateWorkerWorkRequest = RateWorkerPeriodicWorkRequest(this)
        rateWorkerWorkRequest.startWork()
        val rateWorkerOneTimeWorkRequest = RateWorkerOneTimeWorkRequest(this)
        rateWorkerOneTimeWorkRequest.startWork()
    }

    override fun onRestart() {
        stateManager.restoreLastState()
        super.onRestart()
    }

    override fun onStop() {
        stateManager.saveLastState()
        super.onStop()
    }

    override fun onDestroy() {
        rateWorkerWorkRequest.cancelWork()
        super.onDestroy()
    }

    private fun onClickCountryChange (layout: String) : View.OnClickListener {
        return View.OnClickListener {
            if (InternetConnectivity.isOnline(this)){
                stateManager.saveLastState()
                val intent = Intent(this@MainActivity, ActivityCountryList::class.java)
                intent.putExtra(Constants.CURRENCY_CHANGE, layout)
                startActivity(intent)
            }
            else {
                Toast.makeText(this,"No Internet Access",Toast.LENGTH_SHORT).show()
            }
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
