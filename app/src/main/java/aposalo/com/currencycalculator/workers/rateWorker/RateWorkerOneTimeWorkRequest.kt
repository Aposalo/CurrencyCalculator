package aposalo.com.currencycalculator.workers.rateWorker

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import aposalo.com.currencycalculator.workers.WorkerRequest

class RateWorkerOneTimeWorkRequest(override val context: Context) : WorkerRequest() {

    private val oneTimeWorkRequest = OneTimeWorkRequestBuilder<RateWorker>()
        .setConstraints(createConstraints())
        .build()

    override fun startWork() {
        getWorkManagerInstance().enqueue(oneTimeWorkRequest)
    }

    override fun cancelWork() {
        getWorkManagerInstance().cancelWorkById(oneTimeWorkRequest.id)
    }
}