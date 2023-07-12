package aposalo.com.currencycalculator.workers.rateWorker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CurrentCurrencies
import aposalo.com.currencycalculator.domain.repository.RateRepository
import aposalo.com.currencycalculator.utils.InternetConnectivity
import aposalo.com.currencycalculator.utils.StateManager
import com.google.gson.Gson

class RateWorker (private var ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {

    private lateinit var rateRepository : RateRepository

    override suspend fun doWork() : Result {
        val mDb = AppDatabase.getInstance(ctx)
        val stateManager = StateManager(ctx)
        val currentCurrenciesJson = stateManager.getCurrentCurrencies()
        val currentCurrencies = Gson().fromJson(currentCurrenciesJson, CurrentCurrencies::class.java)
        rateRepository = RateRepository(mDb)
        if (InternetConnectivity.isOnline(ctx)) {
            rateRepository.getLatestRateValue(
                from = currentCurrencies.from,
                to = currentCurrencies.to
            )
        }
        return Result.success()
    }
}