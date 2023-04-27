package aposalo.com.currencycalculator.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CurrentCurrencies
import aposalo.com.currencycalculator.domain.repository.RateRepository
import aposalo.com.currencycalculator.util.StateManager
import com.google.gson.Gson

class RateWorker (var ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {

    private lateinit var rateRepository : RateRepository

    override suspend fun doWork() : Result {
        val mDb = AppDatabase.getInstance(ctx)
        val stateManager = StateManager(ctx)
        val currentCurrenciesJson = stateManager.getCurrentCurrencies()
        val gson = Gson()
        val currentCurrencies = gson.fromJson(currentCurrenciesJson, CurrentCurrencies::class.java)
        rateRepository  = RateRepository(mDb)
        rateRepository.getLatestRateValue(
            to = currentCurrencies.to,
            from = currentCurrencies.from)

        mDb?.currencyCalculatorDao()?.clearCurrencies()
        mDb?.countryDao()?.clearCountry()
        return Result.success()
    }
}