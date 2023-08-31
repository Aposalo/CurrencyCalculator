package aposalo.com.currencycalculator.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.listeners.CalculatorListenerSetter
import aposalo.com.currencycalculator.utils.CURRENCY_CHANGE
import aposalo.com.currencycalculator.utils.CURRENCY_TEXT_LABEL
import aposalo.com.currencycalculator.utils.RESULT_TEXT_LABEL
import aposalo.com.currencycalculator.utils.StateManager
import aposalo.com.currencycalculator.utils.isOnline
import aposalo.com.currencycalculator.workers.cleanDatabase.CleanDatabaseWorkRequest
import aposalo.com.currencycalculator.workers.rateWorker.RateWorkerOneTimeWorkRequest
import aposalo.com.currencycalculator.workers.rateWorker.RateWorkerPeriodicWorkRequest
import com.google.android.play.core.review.ReviewManagerFactory

open class MainActivityExtension: AppCompatActivity() {

    protected lateinit var binding: ActivityMainBinding
    private lateinit var stateManager: StateManager
    private lateinit var rateWorkerWorkRequest: RateWorkerPeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reviewManager = ReviewManagerFactory.create(applicationContext)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewManager.launchReviewFlow(this, task.result)
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.resultLayout.setOnClickListener(onClickCountryChange(RESULT_TEXT_LABEL))
        binding.currencyLayout.setOnClickListener(onClickCountryChange(CURRENCY_TEXT_LABEL))
        CalculatorListenerSetter(binding, resources)
        stateManager = StateManager(resources, this, binding)
        stateManager.restoreLastState()
        val cleanDatabaseWorkRequest = CleanDatabaseWorkRequest(this)
        cleanDatabaseWorkRequest.startWork()
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
        stateManager.saveLastState()
        return View.OnClickListener {
            if (isOnline(this)) {
                val intent = Intent(this@MainActivityExtension, ActivityCountryList::class.java)
                intent.putExtra(CURRENCY_CHANGE, layout)
                startActivity(intent)
            }
            else {
                Toast.makeText(this,"No Internet Access", Toast.LENGTH_SHORT).show()
            }
        }
    }

}