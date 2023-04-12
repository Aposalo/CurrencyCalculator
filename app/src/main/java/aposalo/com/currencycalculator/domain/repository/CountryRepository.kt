package aposalo.com.currencycalculator.domain.repository

import androidx.lifecycle.MutableLiveData
import aposalo.com.currencycalculator.domain.server.api.authentication.RetrofitInstance
import aposalo.com.currencycalculator.domain.server.dto.Country
import aposalo.com.currencycalculator.util.Constants
import aposalo.com.currencycalculator.util.Resource
import kotlinx.coroutines.delay

class CountryRepository {

    val data : MutableLiveData<Resource<Country>> = MutableLiveData()//MutableStateFlow<Resource<Country>>(Resource.Success(null))

    suspend fun getCountries() {
        data.postValue(Resource.Loading())
        delay(Constants.DELAY)
        data.postValue(handlePageResponse())
    }

    private suspend fun handlePageResponse() : Resource<Country>{
        val response = RetrofitInstance.api.getCountries()
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success("Success", it)
            }
        }
        val msg = response.message()
        return Resource.Error(msg)
    }

}