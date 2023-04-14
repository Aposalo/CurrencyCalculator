package aposalo.com.currencycalculator.domain.repository

import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.CurrencyCalculatorEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.RetrofitInstance
import aposalo.com.currencycalculator.domain.server.dto.FixerDto
import aposalo.com.currencycalculator.util.Constants.Companion.DELAY
import aposalo.com.currencycalculator.util.Extensions.Companion.getSolution
import aposalo.com.currencycalculator.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyCalculatorRepository(private val mDb: AppDatabase?) {

    private val _dataFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    val data = _dataFlow.asStateFlow()

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

    private suspend fun handlePageResponse() : Resource<FixerDto> {

        if (latestAmount <= 0.0f)
            return Resource.Success(null)

        val latestAmountFormatted = latestAmount.toString().getSolution()

        val resultEntry = mDb?.currencyCalculatorDao()?.getResult(latestTo,
            latestFrom,
            latestAmountFormatted)

            if (resultEntry != null) {
                mDb?.currencyCalculatorDao()?.updateCurrency(resultEntry)
                return Resource.Success(resultEntry.getResult())
            }
            else {
                val response = RetrofitInstance.api.getFixerConvert(latestTo, latestFrom, latestAmountFormatted)

                if (response.isSuccessful) {
                    response.body()?.let {
                            resultResponse ->
                                val resultAmountString = resultResponse.query.amount.toString().getSolution()
                                val resultFrom = resultResponse.query.from
                                val resultTo = resultResponse.query.to

                                if (latestAmountFormatted == resultAmountString && latestTo == resultTo && latestFrom == resultFrom) {
                                    val latestResult = resultResponse.result.toString()
                                    val entry = CurrencyCalculatorEntry(to = resultTo, from = resultFrom, amount = resultAmountString, result = latestResult)
                                    mDb?.currencyCalculatorDao()?.insertCurrency(entry)
                                    return Resource.Success(latestResult)
                                }
                                return Resource.Loading()
                    }
                }
                val msg = response.message()
                return Resource.Error(msg)
            }
    }
}