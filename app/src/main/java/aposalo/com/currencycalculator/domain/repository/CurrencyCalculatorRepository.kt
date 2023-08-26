package aposalo.com.currencycalculator.domain.repository

import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.currency.CurrencyCalculatorEntry
import aposalo.com.currencycalculator.domain.local.rate.LatestRateEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.ApiInstance
import aposalo.com.currencycalculator.domain.server.dto.FixerDto
import aposalo.com.currencycalculator.utils.API_EXCEEDED_CALLS_CODE
import aposalo.com.currencycalculator.utils.CalculationExtensions.getSolution
import aposalo.com.currencycalculator.utils.DELAY
import aposalo.com.currencycalculator.utils.Resource
import io.sentry.Sentry
import kotlinx.coroutines.delay

class CurrencyCalculatorRepository(private val mDb: AppDatabase?) {

    suspend fun handlePageResponse(
        latestAmount: Float,
        latestFrom: String,
        latestTo: String
    ): Resource<FixerDto> {

        if (latestAmount <= 0.0f) return Resource.Success(null)

        val latestAmountFormatted = latestAmount.toString().getSolution()

        val resultEntry = mDb?.currencyCalculatorDao()?.getResult(
            from = latestFrom,
            to = latestTo,
            amount = latestAmountFormatted
        )

        try {
            if (resultEntry != null) {
                mDb?.currencyCalculatorDao()?.updateCurrency(resultEntry)
                return Resource.Success(resultEntry.result)
            }
            else {
                val localRateDb = mDb?.latestRateDao()?.getResult (
                    from = latestFrom,
                    to = latestTo
                )
                if (localRateDb != null) {
                    return Resource.Success("0")
                }

                delay(DELAY)

                val response = ApiInstance.api.getFixerConvert (
                    from = latestFrom,
                    to = latestTo,
                    amount = latestAmountFormatted
                )

                val code = response.code()
                if(code == API_EXCEEDED_CALLS_CODE)
                {
                    Sentry.captureMessage("API exceeded calls, please change key")
                    return Resource.Error("Error")
                }

                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val resultRealAmount = resultResponse.query.amount
                        val resultAmountString = resultRealAmount.toString().getSolution()
                        val resultFrom = resultResponse.query.from
                        val resultTo = resultResponse.query.to
                        val latestDate = resultResponse.info.timestamp
                        val latestRate = resultResponse.info.quote

                        if (latestAmountFormatted == resultAmountString &&
                            latestTo == resultTo &&
                            latestFrom == resultFrom) {

                            val latestResult = resultResponse.result.toString()

                            val entry = CurrencyCalculatorEntry(
                                from = resultFrom,
                                to = resultTo,
                                amount = resultAmountString,
                                latestDate = latestDate,
                                result = latestResult
                            )

                            mDb?.currencyCalculatorDao()?.insertCurrency(entry)

                            val rateDb = mDb?.latestRateDao()?.getResult(resultTo, resultFrom)
                            if (rateDb == null) {
                                val latestRateEntry = LatestRateEntry(
                                    from = resultFrom,
                                    to = resultTo,
                                    rate = latestRate,
                                    latestDate = latestDate
                                )
                                mDb?.latestRateDao()?.insertLatestRate(latestRateEntry)
                            }
                            else {
                                rateDb.rate = (latestRate)
                                rateDb.latestDate = (latestDate)
                                mDb?.latestRateDao()?.updateLatestRate(rateDb)
                            }
                            return Resource.Success(latestResult)
                        }
                        return Resource.Loading()
                    }
                }
                response.message().let{ msg ->
                    Sentry.captureMessage(msg)
                    return Resource.Error(msg)
                }
            }
        }
        catch (e: Exception) {
            e.message?.let { Sentry.captureMessage(it) }
        }
        return Resource.Error("Error")
    }

}