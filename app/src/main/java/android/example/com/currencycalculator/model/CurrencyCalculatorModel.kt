package android.example.com.currencycalculator.model

import android.example.com.currencycalculator.repository.CurrencyCalculatorRepository
import android.example.com.currencycalculator.repository.dto.FixerDto
import android.example.com.currencycalculator.util.Resource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class CurrencyCalculatorModel() : ViewModel() {

    private val _dataFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    private val repository: CurrencyCalculatorRepository = CurrencyCalculatorRepository()
    private var finalResponse: Response<FixerDto>? = null

    val data = _dataFlow.asStateFlow()

    fun getUserPage(to: String, from: String, amount: Float) {
        viewModelScope.launch {
            finalResponse = null
            if (amount > 0.0f) {
                _dataFlow.value = Resource.Loading()
                val response = repository.getFixerConvert(to, from, amount)
                finalResponse = response
            }
            _dataFlow.value = handlePageResponse()
        }
    }

    private fun handlePageResponse() : Resource<FixerDto> {

        if (finalResponse == null)
            return Resource.Success(null)

        if (finalResponse!!.isSuccessful) {
            finalResponse!!.body()?.let {
                    resultResponse ->
                        return Resource.Success(resultResponse)
            }
        }
        val msg = finalResponse!!.message()
        return Resource.Error(msg)
    }
}