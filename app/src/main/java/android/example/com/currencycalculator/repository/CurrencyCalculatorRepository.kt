package android.example.com.currencycalculator.repository

import android.example.com.currencycalculator.api.authentication.RetrofitInstance
import android.example.com.currencycalculator.repository.dto.FixerDto
import android.example.com.currencycalculator.util.Resource
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
        _dataFlow.emit(handlePageResponse())
    }

    private suspend fun handlePageResponse() : Resource<FixerDto> {

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