package aposalo.com.currencycalculator.workers.cleanDatabase

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import aposalo.com.currencycalculator.workers.WorkerRequest

class CleanDatabaseWorkRequest(override val context: Context) : WorkerRequest(context) {

    private val oneTimeWorkRequest = OneTimeWorkRequestBuilder<CleanDatabaseWorker>()
        .setConstraints(createConstraints())
        .build()

    override fun startWork() {
        getWorkManagerInstance().enqueue(oneTimeWorkRequest)
    }

    override fun cancelWork() {
        getWorkManagerInstance().cancelWorkById(oneTimeWorkRequest.id)
    }

}