package aposalo.com.currencycalculator.workers.cleanDatabase

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import aposalo.com.currencycalculator.domain.local.AppDatabase

class CleanDatabaseWorker(var ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {


    override suspend fun doWork(): Result {
        val mDb = AppDatabase.getInstance(ctx)
        mDb?.currencyCalculatorDao()?.clearCurrencies()
        mDb?.countryDao()?.clearCountry()
        return Result.success()
    }


}