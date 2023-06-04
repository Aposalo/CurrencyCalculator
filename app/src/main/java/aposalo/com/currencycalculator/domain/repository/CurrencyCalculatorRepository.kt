package aposalo.com.currencycalculator.domain.repository

import android.annotation.SuppressLint
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.currency.CurrencyCalculatorEntry
import aposalo.com.currencycalculator.domain.local.rate.LatestRateEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.ApiInstance
import aposalo.com.currencycalculator.domain.server.dto.FixerDto
import aposalo.com.currencycalculator.util.Constants.Companion.DELAY
import aposalo.com.currencycalculator.util.Extensions.Companion.getSolution
import aposalo.com.currencycalculator.util.Resource
import io.sentry.Sentry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyCalculatorRepository(private val mDb: AppDatabase?) {

    private val _dataCurrencyCalculatorFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    val dataCurrencyCalculator = _dataCurrencyCalculatorFlow.asStateFlow()

    private var latestTo : String = ""
    private var latestFrom : String = ""
    private var latestAmount : Float = 0.0f

    suspend fun getCurrencyValue(to: String, from: String, amount: Float) {
        _dataCurrencyCalculatorFlow.emit(Resource.Loading())
        latestTo = to
        latestFrom = from
        latestAmount = amount
        _dataCurrencyCalculatorFlow.emit(handlePageResponse())
    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun handlePageResponse() : Resource<FixerDto> {
        if (latestAmount <= 0.0f)
            return Resource.Success(null)

        val latestAmountFormatted = latestAmount.toString().getSolution()

        val resultEntry = mDb?.currencyCalculatorDao()?.getResult(
            latestTo,
            latestFrom,
            latestAmountFormatted
        )

        try {
            if (resultEntry != null) {
                mDb?.currencyCalculatorDao()?.updateCurrency(resultEntry)
                return Resource.Success(resultEntry.getResult())
            }
            else {

                val rateDb = mDb?.latestRateDao()?.getResult (
                    to = latestTo,
                    from = latestFrom
                )
                if (rateDb != null) {
                    return Resource.Success("0")
                }

                delay(DELAY)

                val response = ApiInstance.longApi.getFixerConvert (
                    to = latestTo,
                    from = latestFrom,
                    amount = latestAmountFormatted
                )

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
                                to = resultTo,
                                from = resultFrom,
                                amount = resultAmountString,
                                latestDate = latestDate,
                                result = latestResult
                            )

                            mDb?.currencyCalculatorDao()?.insertCurrency(entry)

                            val rateDb = mDb?.latestRateDao()?.getResult(resultTo, resultFrom)
                            if (rateDb == null) {
                                val latestRateEntry = LatestRateEntry(
                                    to = resultTo,
                                    from = resultFrom,
                                    rate = latestRate,
                                    latestDate = latestDate
                                )
                                mDb?.latestRateDao()?.insertLatestRate(latestRateEntry)
                            }
                            else {
                                rateDb.setRate(latestRate)
                                rateDb.setLatestDate(latestDate)
                                mDb?.latestRateDao()?.updateLatestRate(rateDb)
                            }

                            return Resource.Success(latestResult)
                        }
                        return Resource.Loading()
                    }
                }
                val msg = response.message()
                Sentry.captureMessage(msg)
                return Resource.Error(msg)
            }
        }
        catch (e: Exception) {
            e.message?.let { Sentry.captureMessage(it) }
        }
        return Resource.Error("Error")
    }

}