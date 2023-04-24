package aposalo.com.currencycalculator.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CurrentCurrencies
import aposalo.com.currencycalculator.util.StateManager
import com.google.gson.Gson

class RateWorker (var ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {

    override suspend fun doWork() : Result {
        Log.d("TAG", "doWork: WORK STARTED ")
        val mDb = AppDatabase.getInstance(ctx)
        val stateManager = StateManager(ctx)
        val currentCurrenciesJson = stateManager.getCurrentCurrencies()
        val gson = Gson()
        var currentCurrencies = gson.fromJson(currentCurrenciesJson, CurrentCurrencies::class.java)



        return Result.success()
    }
}