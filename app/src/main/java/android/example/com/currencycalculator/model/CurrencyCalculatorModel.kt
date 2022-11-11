package android.example.com.currencycalculator.model

import android.example.com.currencycalculator.repository.CurrencyCalculatorRepository
import android.example.com.currencycalculator.repository.dto.FixerDto
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

    private var finalResponse: Response<FixerDto>? = null

    fun getUserPage(to: String, from: String, amount: Float) =  viewModelScope.launch {
        previousTo = to
        previousFrom = from
        previousAmount = amount
        if (amount <= 0.0f) {
            finalResponse = null
            data.postValue(handlePageResponse())
        }
        else {
            data.postValue(Resource.Loading())
            val response = repository.getFixerConvert(to, from, amount)
            if (response.isCurrentResponse()){
                finalResponse = response
                data.postValue(handlePageResponse())
            }
        }
    }

    private fun Response<FixerDto>?.isCurrentResponse(): Boolean {
        return this?.body()?.query?.amount == previousAmount
                && this.body()?.query?.to == previousTo
                && this.body()?.query?.from == previousFrom
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