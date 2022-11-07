package android.example.com.currencycalculator.model

import android.example.com.currencycalculator.repository.dto.FixerDto
import android.example.com.currencycalculator.repository.CurrencyCalculatorRepository
import android.example.com.currencycalculator.util.Resource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response

class CurrencyCalculatorModel(
    private val repository: CurrencyCalculatorRepository,
    private var previousAmount: Float ,
    private var previousTo: String,
    private var previousFrom: String,
) : ViewModel() {

    val data: MutableLiveData<Resource<FixerDto>> = MutableLiveData()

    fun getUserPage(to: String, from: String, amount: Float) =  viewModelScope.launch {
        previousTo = to
        previousFrom = from
        previousAmount = amount
        if (amount <= 0.0f)
            data.postValue(handlePageResponse(null))
        else {
            data.postValue(Resource.Loading())
            val response = repository.getFixerConvert(to, from, amount)
            if (isCurrentResponse(response))
                data.postValue(handlePageResponse(response))
        }
    }

    private fun isCurrentResponse(response: Response<FixerDto>?): Boolean {
        return response?.body()?.query?.amount == previousAmount
                && response.body()?.query?.to == previousTo
                && response.body()?.query?.from == previousFrom
    }

    private fun handlePageResponse(response: Response<FixerDto>?) : Resource<FixerDto> {

        if (response == null)
            return Resource.Success(null)

        if (response.isSuccessful) {
            response.body()?.let {
                    resultResponse -> return Resource.Success(resultResponse)
            }
        }
        val msg = response.message()
        return Resource.Error(msg)
    }
}