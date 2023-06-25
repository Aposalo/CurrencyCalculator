package aposalo.com.currencycalculator.domain.repository

import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.rate.LatestRateEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.ApiInstance
import aposalo.com.currencycalculator.util.RateConvertor.Companion.getConvertedQuotes
import io.sentry.Sentry

class RateRepository(private val mDb : AppDatabase?)  {

    private var latestRateFrom : String = ""
    private var latestRateTo : String = ""

    suspend fun getLatestRateValue(from: String, to: String) {
        latestRateFrom = from
        latestRateTo = to
        handleLatestRateInDatabase()
    }

    private suspend fun handleLatestRateInDatabase() {
        try {
                val response = ApiInstance.longApi.getLatestRates (
                    source = latestRateFrom)
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val quotes = getConvertedQuotes(resultResponse.quotes, latestRateFrom)
                        quotes.forEach { mapEntry ->
                            val rateDb = mDb?.latestRateDao()?.getResult (
                                from = latestRateFrom,
                                to = mapEntry.key
                            )
                            if (rateDb == null) {
                                val latestRateEntry = LatestRateEntry (
                                    from = latestRateFrom,
                                    to = mapEntry.key,
                                    rate = mapEntry.value,
                                    latestDate = resultResponse.timestamp
                                )
                                mDb?.latestRateDao()?.insertLatestRate(latestRateEntry)//TODO insertorupdate function
                            }
                            else {
                                rateDb.setRate(mapEntry.value)
                                rateDb.setLatestDate(resultResponse.timestamp)
                                mDb?.latestRateDao()?.updateLatestRate(rateDb)
                            }
                        }
                    }
                }
        }
        catch (e : Exception) {
            e.message?.let { Sentry.captureMessage(it) }
        }
    }
}