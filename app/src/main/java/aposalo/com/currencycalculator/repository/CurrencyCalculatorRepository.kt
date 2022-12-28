package aposalo.com.currencycalculator.repository

import aposalo.com.currencycalculator.api.authentication.RetrofitInstance
import aposalo.com.currencycalculator.repository.dto.FixerDto
import aposalo.com.currencycalculator.util.Constants.Companion.DELAY
import aposalo.com.currencycalculator.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyCalculatorRepository {

    private val _dataFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    val data = _dataFlow.asStateFlow()

    private var latestTo : String = ""
    private var latestFrom : String = ""
    private var latestAmount : Float = 0.0f
    private var latestResult : String = ""
    private var isTheSameConvert = false

    suspend fun getFixerConvert(to: String, from: String, amount: Float) {
        _dataFlow.emit(Resource.Loading())
        isTheSameConvert = latestTo == to &&
                latestFrom == from &&
                latestAmount == amount
        latestTo = to
        latestFrom = from
        latestAmount = amount
        delay(DELAY)
        _dataFlow.emit(handlePageResponse())
    }

    private suspend fun handlePageResponse() : Resource<FixerDto> {

        if (latestAmount <= 0.0f)
            return Resource.Success(null)

        if (isTheSameConvert)
            return Resource.Success(latestResult)

        val response = RetrofitInstance.api.getFixerConvert(latestTo, latestFrom, latestAmount)

        if (response.isSuccessful) {
            response.body()?.let {
                    resultResponse ->
                latestResult = resultResponse.result.toString()
                return Resource.Success(latestResult)
            }
        }
        val msg = response.message()
        return Resource.Error(msg)
    }

}