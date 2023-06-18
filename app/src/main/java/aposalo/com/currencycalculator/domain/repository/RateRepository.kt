package aposalo.com.currencycalculator.domain.repository

import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.rate.LatestRateEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.ApiInstance
import aposalo.com.currencycalculator.domain.server.dto.Rate
import aposalo.com.currencycalculator.util.Resource
import io.sentry.Sentry

class RateRepository(private val mDb : AppDatabase?)  {

    private var latestRateTo : String = ""
    private var latestRateFrom : String = ""

    suspend fun getLatestRateValue(to: String, from: String) {
        latestRateTo = to
        latestRateFrom = from
        handleLatestRateResponse()
    }

    private suspend fun handleLatestRateResponse() : Resource<Rate> {
        try {
                val response = ApiInstance.longApi.getLatestRates (
                    base = latestRateFrom,
                    symbols = latestRateTo
                )
                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        resultResponse.quotes.forEach { mapEntry ->
                            val rateDb = mDb?.latestRateDao()?.getResult (
                                to = latestRateTo,
                                from = latestRateFrom
                            )
                            if (rateDb == null) {
                                val latestRateEntry = LatestRateEntry (
                                    to = latestRateTo,
                                    from = latestRateFrom,
                                    rate = mapEntry.value,
                                    latestDate = resultResponse.timestamp
                                )
                                mDb?.latestRateDao()?.insertLatestRate(latestRateEntry)
                            }
                            else {
                                rateDb.setRate(mapEntry.value)
                                rateDb.setLatestDate(resultResponse.timestamp)
                                mDb?.latestRateDao()?.updateLatestRate(rateDb)
                            }
                            return Resource.Success(mapEntry.value.toString())
                        }
                    }
                }

        }
        catch (e : Exception) {
            e.message?.let { Sentry.captureMessage(it) }
        }
        return Resource.Error("Rate Repository Error")
    }
}