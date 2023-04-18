package aposalo.com.currencycalculator.domain.repository

import android.annotation.SuppressLint
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.currency.CurrencyCalculatorEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.RetrofitInstance
import aposalo.com.currencycalculator.domain.server.dto.FixerDto
import aposalo.com.currencycalculator.util.Constants.Companion.DELAY
import aposalo.com.currencycalculator.util.Extensions.Companion.getSolution
import aposalo.com.currencycalculator.util.Resource
import io.sentry.Sentry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyCalculatorRepository(private val mDb: AppDatabase?) {

    private val _dataFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    val data = _dataFlow.asStateFlow()

    private val _dataFlowInitValue = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    val dataInitValue = _dataFlowInitValue.asStateFlow()

    private var latestTo : String = ""
    private var latestFrom : String = ""
    private var latestAmount : Float = 0.0f

    suspend fun getFixerConvert(to: String, from: String, amount: Float) {
        _dataFlow.emit(Resource.Loading())
        latestTo = to
        latestFrom = from
        latestAmount = amount
        delay(DELAY)
        _dataFlow.emit(handlePageResponse())
    }

    suspend fun getFixerConvert(to: String, from: String) {
        _dataFlowInitValue.emit(Resource.Loading())
        latestTo = to
        latestFrom = from
        val amount = 1.0f
        delay(DELAY)
        _dataFlowInitValue.emit(handlePageResponse(amount))
    }

    private suspend fun handlePageResponse(amount: Float) : Resource<FixerDto> {

        val latestAmountFormatted = amount.toString().getSolution()

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
                val response = RetrofitInstance.api.getFixerConvert(
                    latestTo,
                    latestFrom,
                    latestAmountFormatted
                )

                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val resultAmountString =
                            resultResponse.query.amount.toString().getSolution()
                        val resultFrom = resultResponse.query.from
                        val resultTo = resultResponse.query.to

                        if (latestAmountFormatted == resultAmountString && latestTo == resultTo && latestFrom == resultFrom) {
                            val latestResult = resultResponse.result.toString()
                            val entry = CurrencyCalculatorEntry(
                                to = resultTo,
                                from = resultFrom,
                                amount = resultAmountString,
                                result = latestResult
                            )
                            mDb?.currencyCalculatorDao()?.insertCurrency(entry)
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
                val response = RetrofitInstance.api.getFixerConvert(
                    latestTo,
                    latestFrom,
                    latestAmountFormatted
                )

                if (response.isSuccessful) {
                    response.body()?.let { resultResponse ->
                        val resultAmountString =
                            resultResponse.query.amount.toString().getSolution()
                        val resultFrom = resultResponse.query.from
                        val resultTo = resultResponse.query.to

                        if (latestAmountFormatted == resultAmountString && latestTo == resultTo && latestFrom == resultFrom) {
                            val latestResult = resultResponse.result.toString()
                            val entry = CurrencyCalculatorEntry(
                                to = resultTo,
                                from = resultFrom,
                                amount = resultAmountString,
                                result = latestResult
                            )
                            mDb?.currencyCalculatorDao()?.insertCurrency(entry)
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