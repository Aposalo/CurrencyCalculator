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
    private var response: Response<FixerDto>? = null

    val data = _dataFlow.asStateFlow()

    fun getUserPage(to: String, from: String, amount: Float) {
        viewModelScope.launch {
            response = null
            if (amount > 0.0f) {
                _dataFlow.emit(Resource.Loading())
                response = repository.getFixerConvert(to, from, amount)
            }
            _dataFlow.emit(handlePageResponse())
        }
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