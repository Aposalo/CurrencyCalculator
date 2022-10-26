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
            val response = repository.getFixerConvert(to, from, amount)
            previousAmount = amount
            previousTo = to
            previousFrom = from
            data.postValue(handlePageResponse(response))
        }
    }

    fun clearCalculator() {
        previousAmount = 0.0f
    }

    private fun handlePageResponse(response: Response<Fixer>) : Resource<Fixer> {

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