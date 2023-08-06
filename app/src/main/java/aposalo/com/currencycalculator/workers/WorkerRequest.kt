package aposalo.com.currencycalculator.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.WorkManager

abstract class WorkerRequest {

    protected abstract val context: Context

    fun createConstraints() = Constraints
        .Builder()
        .build()

    fun getWorkManagerInstance(): WorkManager {
        return WorkManager.getInstance(context)
    }

    abstract fun startWork()

    abstract fun cancelWork()

    fun cancelAllWork(){
        getWorkManagerInstance().cancelAllWork()
    }

}