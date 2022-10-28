package android.example.com.currencycalculator.model

import android.example.com.currencycalculator.api.dataclass.Fixer
import android.example.com.currencycalculator.repository.CurrencyCalculatorRepository
import android.example.com.currencycalculator.util.Resource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response

class CurrencyCalculatorModel(
    private val repository: CurrencyCalculatorRepository
) : ViewModel() {

    var response: Fixer? = null
    val data: MutableLiveData<Resource<Fixer>> = MutableLiveData()
    var previousAmount: Float = 0.0f
    var previousTo: String = "GBP"
    var previousFrom: String = "EUR"

    init {
        getUserPage(previousTo, previousFrom, previousAmount)
    }

    fun getUserPage(to: String, from: String, amount: Float) =  viewModelScope.launch {
        if (previousTo != to || previousFrom != from || previousAmount != amount) {
            data.postValue(Resource.Loading())
            previousAmount = amount
            previousTo = to
            previousFrom = from
            if (amount <= 0.0f)
                data.postValue(handlePageResponse(null))
            val response = repository.getFixerConvert(to, from, amount)
            data.postValue(handlePageResponse(response))
        }
    }

    fun isCurrentAmount(): Boolean {
        return response?.query?.amount == previousAmount
    }

    private fun handlePageResponse(response: Response<Fixer>?) : Resource<Fixer> {

        if (response == null)
            return Resource.Success(null)

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                this.response = resultResponse
                return Resource.Success(resultResponse)
            }
        }
        val msg = response.message()
        return Resource.Error(msg)
    }
}