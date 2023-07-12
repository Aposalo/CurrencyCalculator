package aposalo.com.currencycalculator.domain.repository

import android.annotation.SuppressLint
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.currency.CurrencyCalculatorEntry
import aposalo.com.currencycalculator.domain.local.rate.LatestRateEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.ApiInstance
import aposalo.com.currencycalculator.domain.server.dto.FixerDto
import aposalo.com.currencycalculator.util.Constants.Companion.DELAY
import aposalo.com.currencycalculator.util.CalculationExtensions.Companion.getSolution
import aposalo.com.currencycalculator.util.Constants
import aposalo.com.currencycalculator.util.Resource
import io.sentry.Sentry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyCalculatorRepository(private val mDb: AppDatabase?) {

    private val _dataCurrencyCalculatorFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    val dataCurrencyCalculator = _dataCurrencyCalculatorFlow.asStateFlow()

    private var latestFrom : String = ""
    private var latestTo : String = ""
    private var latestAmount : Float = 0.0f

    suspend fun getCurrencyValue(from: String, to: String, amount: Float) {
        _dataCurrencyCalculatorFlow.emit(Resource.Loading())
        latestFrom = from
        latestTo = to
        latestAmount = amount
        _dataCurrencyCalculatorFlow.emit(handlePageResponse())
    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun handlePageResponse() : Resource<FixerDto> {
        if (latestAmount <= 0.0f)
            return Resource.Success(null)

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

                val response = ApiInstance.longApi.getFixerConvert (
                    from = latestFrom,
                    to = latestTo,
                    amount = latestAmountFormatted
                )

                val code = response.code()
                if(code == Constants.API_EXCEEDED_CALLS_CODE)
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