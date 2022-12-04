package android.example.com.currencycalculator.repository

import android.example.com.currencycalculator.api.authentication.RetrofitInstance
import android.example.com.currencycalculator.repository.dto.FixerDto
import android.example.com.currencycalculator.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response

class CurrencyCalculatorRepository {

    private val _dataFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    private var response: Response<FixerDto>? = null
    val data = _dataFlow.asStateFlow()

    suspend fun getFixerConvert(to: String, from: String, amount: Float) {
        if (amount > 0.0f) {
            _dataFlow.emit(Resource.Loading())
            response = RetrofitInstance.api.getFixerConvert(to, from, amount)
        }
        _dataFlow.emit(handlePageResponse())
    }

    private fun handlePageResponse() : Resource<FixerDto> {

        if (response == null)
            return Resource.Success(null)

        if (response!!.isSuccessful) {
            response!!.body()?.let {
                    resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val msg = response!!.message()
        return Resource.Error(msg)
    }

}