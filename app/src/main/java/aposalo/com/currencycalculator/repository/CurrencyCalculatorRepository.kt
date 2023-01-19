package aposalo.com.currencycalculator.repository

import aposalo.com.currencycalculator.api.authentication.RetrofitInstance
import aposalo.com.currencycalculator.repository.dto.FixerDto
import aposalo.com.currencycalculator.util.Constants.Companion.DELAY
import aposalo.com.currencycalculator.util.Extensions.Companion.toTwoDecimalsString
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
    private var latestResult : String = "0"

    suspend fun getFixerConvert(to: String, from: String, amount: Float) {
        _dataFlow.emit(Resource.Loading())
        latestTo = to
        latestFrom = from
        latestAmount = amount
        delay(DELAY)
        _dataFlow.emit(handlePageResponse())
    }

    private suspend fun handlePageResponse() : Resource<FixerDto> {

        if (latestAmount <= 0.0f){
            latestResult = "0"
            return Resource.Success(null)
        }


        /*****************************************************************/

        //TODO first check the cache

        /***************************************************************/


        val response = RetrofitInstance.api.getFixerConvert(latestTo, latestFrom, latestAmount.toString().toTwoDecimalsString())

        if (response.isSuccessful) {
            response.body()?.let {
                    resultResponse ->

                val resultAmountString = resultResponse.query.amount.toString().toTwoDecimalsString()
                val resultFrom = resultResponse.query.from
                val resultTo = resultResponse.query.to

                if (latestAmount.toString().toTwoDecimalsString() == resultAmountString && latestTo == resultTo && latestFrom == resultFrom){
                    latestResult = resultResponse.result.toString()
                    return Resource.Success(latestResult)
                }
                return Resource.Loading()
            }
        }
        val msg = response.message()
        return Resource.Error(msg)

    }

}