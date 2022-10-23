package android.example.com.userleaderboard.model

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

    var pageResponse: Fixer? = null
    val pageData: MutableLiveData<Resource<Fixer>> = MutableLiveData()

    init {
        getUserPage("GBP", "EUR",0)
    }

    fun getUserPage(to: String, from: String, amount: Int) =  viewModelScope.launch {
        pageData.postValue(Resource.Loading())
        val response = repository.getFixerConvert(to, from, amount)
        pageData.postValue(handlePageResponse(response))
    }

    private fun handlePageResponse(response: Response<Fixer>) : Resource<Fixer> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pageResponse = resultResponse
                return Resource.Success(resultResponse)
            }
        }
        var msg = response.message()
        return Resource.Error(msg)
    }
}