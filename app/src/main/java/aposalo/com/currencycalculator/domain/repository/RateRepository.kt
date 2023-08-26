package aposalo.com.currencycalculator.domain.repository

import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.rate.LatestRateEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.ApiInstance
import aposalo.com.currencycalculator.utils.API_EXCEEDED_CALLS_CODE
import io.sentry.Sentry

class RateRepository(private var mDb : AppDatabase?) {


    private var latestRateFrom  = String()
    private var latestRateTo  = String()

    suspend fun getLatestRateValue(from: String, to: String) {
        latestRateFrom = from
        latestRateTo = to
        handleLatestRateInDatabase()
    }

    private fun Map<String,Float>.getConvertedQuotes():Map<String,Float> {
        val convertedQuotes = mutableMapOf<String,Float> ()
        this.forEach { (k,v) ->
            val newKey = k.removePrefix(latestRateFrom)
            convertedQuotes[newKey] = v
        }
        return convertedQuotes
    }

    private suspend fun handleLatestRateInDatabase() {
        try {
            val response = ApiInstance.api.getLatestRates (
                source = latestRateFrom
            )
            val code = response.code()
            if(code == API_EXCEEDED_CALLS_CODE)
            {
                Sentry.captureMessage("API exceeded calls, please change key")
            }
            else if (response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    val quotes = resultResponse.quotes.getConvertedQuotes()
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
                            rateDb.rate = (mapEntry.value)
                            rateDb.latestDate = (resultResponse.timestamp)
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