package android.aposalo.com.currencycalculator.repository

import android.aposalo.com.currencycalculator.api.authentication.RetrofitInstance
import android.aposalo.com.currencycalculator.repository.dto.FixerDto
import android.aposalo.com.currencycalculator.util.Constants.Companion.DELAY
import android.aposalo.com.currencycalculator.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrencyCalculatorRepository {

    private val _dataFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    val data = _dataFlow.asStateFlow()

    private lateinit var latestTo : String
    private lateinit var latestFrom : String
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

        if (latestAmount == 0.0f)
            return Resource.Success(null)

        val response = RetrofitInstance.api.getFixerConvert(latestTo, latestFrom, latestAmount)

        if (response.isSuccessful) {
            response.body()?.let {
                    resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val msg = response.message()
        return Resource.Error(msg)
    }

}