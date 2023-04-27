package aposalo.com.currencycalculator.workers

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import aposalo.com.currencycalculator.util.Constants
import java.util.concurrent.TimeUnit

class WorkerRequest(private val context : Context) {

    private fun createConstraints() = Constraints
        .Builder()
        .build()

    private fun createWorkRequest() = PeriodicWorkRequestBuilder<RateWorker>(1, TimeUnit.DAYS)
        .setInputData(Data.EMPTY)
        .setConstraints(createConstraints())
        .setBackoffCriteria(BackoffPolicy.LINEAR,
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS)
        .build()

    fun startWork() {
        val work = createWorkRequest()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(Constants.RATE_WORKER, ExistingPeriodicWorkPolicy.UPDATE,work)
    }
}