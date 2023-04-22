package aposalo.com.currencycalculator.domain.repository

import android.annotation.SuppressLint
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.currency.CurrencyCalculatorEntry
import aposalo.com.currencycalculator.domain.local.rate.LatestRateEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.RetrofitInstance
import aposalo.com.currencycalculator.domain.server.dto.FixerDto
import aposalo.com.currencycalculator.domain.server.dto.Rate
import aposalo.com.currencycalculator.util.Constants.Companion.DELAY
import aposalo.com.currencycalculator.util.Extensions.Companion.getDateTime
import aposalo.com.currencycalculator.util.Extensions.Companion.getSolution
import aposalo.com.currencycalculator.util.Resource
import io.sentry.Sentry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyCalculatorRepository(private val mDb: AppDatabase?) {

    private val _dataCurrencyCalculatorFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    val dataCurrencyCalculator = _dataCurrencyCalculatorFlow.asStateFlow()

    private val _dataLatestRateFlow = MutableStateFlow<Resource<Rate>>(Resource.Success(null))
    val dataLatestRate = _dataLatestRateFlow.asStateFlow()

    private var latestRateTo : String = ""
    private var latestRateFrom : String = ""

    private var latestTo : String = ""
    private var latestFrom : String = ""
    private var latestAmount : Float = 0.0f

    suspend fun getLatestRateValue(to: String, from: String) {
        latestRateTo = to
        latestRateFrom = from
        delay(DELAY)
        _dataLatestRateFlow.emit(handleLatestRateResponse())
    }

    private suspend fun handleLatestRateResponse() : Resource<Rate> {
        val resultEntry = mDb?.latestRateDao()?.getResult(
            latestRateTo,
            latestRateFrom,
        )
        try {
            if (resultEntry != null) {
                mDb?.latestRateDao()?.updateLatestRate(resultEntry)
                return Resource.Success(resultEntry.toString())
            }
            else{
                val response = RetrofitInstance.lateApi.getLatestRates(base = latestFrom, symbols = latestRateTo)
                if (response.isSuccessful) {
                    response.body()?.let {resultResponse ->
                        resultResponse.rates.forEach{mapEntry ->
                            val latestRateEntry = LatestRateEntry(to = mapEntry.key,
                                from = latestRateFrom,
                                rate = mapEntry.value,
                                latestDate = resultResponse.timestamp.getDateTime()
                            )
                            mDb?.latestRateDao()?.updateLatestRate(latestRateEntry)
                        }
                    }
                }
            }
        }
        catch (e : Exception) {
            e.message?.let { Sentry.captureMessage(it) }
        }
        return Resource.Error("Error")
    }

    suspend fun getCurrencyValue(to: String, from: String, amount: Float) {
        _dataCurrencyCalculatorFlow.emit(Resource.Loading())
        latestTo = to
        latestFrom = from
        latestAmount = amount
        delay(DELAY)
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
                val response = RetrofitInstance.lateApi.getFixerConvert(
                    latestTo,
                    latestFrom,
                    latestAmountFormatted
                )

                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val resultRealAmount = resultResponse.query.amount
                        val resultAmountString = resultRealAmount.toString().getSolution()
                        val resultFrom = resultResponse.query.from
                        val resultTo = resultResponse.query.to
                        val latestDate = resultResponse.info.timestamp.getDateTime()
                        val latestRate = resultResponse.info.rate
                        if (latestAmountFormatted == resultAmountString && latestTo == resultTo && latestFrom == resultFrom) {
                            val latestResult = resultResponse.result.toString()
                            val entry = CurrencyCalculatorEntry(
                                to = resultTo,
                                from = resultFrom,
                                amount = resultAmountString,
                                latestDate = latestDate,
                                result = latestResult
                            )
                            val latestRateEntry = LatestRateEntry(to = resultTo,
                                from = resultFrom,
                                rate = latestRate,
                                latestDate = latestDate
                            )
                            mDb?.currencyCalculatorDao()?.insertCurrency(entry)
                            mDb?.latestRateDao()?.updateLatestRate(latestRateEntry)
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