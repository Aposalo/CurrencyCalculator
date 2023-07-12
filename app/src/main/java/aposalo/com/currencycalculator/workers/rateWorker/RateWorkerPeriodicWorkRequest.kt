package aposalo.com.currencycalculator.workers.rateWorker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import aposalo.com.currencycalculator.utils.Constants
import aposalo.com.currencycalculator.workers.WorkerRequest
import java.util.concurrent.TimeUnit

class RateWorkerPeriodicWorkRequest(override val context: Context) : WorkerRequest(context) {

    private val periodicWorkRequest = PeriodicWorkRequestBuilder<RateWorker>(
        1, TimeUnit.DAYS,
        10, TimeUnit.MINUTES)
        .setInputData(Data.EMPTY)
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS)
        .setConstraints(createConstraints())
        .build()

    override fun startWork() {
        getWorkManagerInstance().enqueueUniquePeriodicWork(
            Constants.RATE_WORKER,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest)
    }

    override fun cancelWork() {
        getWorkManagerInstance().cancelWorkById(periodicWorkRequest.id)
    }

}