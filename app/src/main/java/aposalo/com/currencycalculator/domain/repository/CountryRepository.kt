package aposalo.com.currencycalculator.domain.repository

import androidx.lifecycle.MutableLiveData
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.CountryEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.RetrofitInstance
import aposalo.com.currencycalculator.domain.server.dto.Country
import aposalo.com.currencycalculator.util.Constants
import aposalo.com.currencycalculator.util.Resource
import kotlinx.coroutines.delay

class CountryRepository(private val mDb: AppDatabase?) {

    val data : MutableLiveData<Resource<Country>> = MutableLiveData()

    suspend fun getCountries() {
        data.postValue(Resource.Loading())
        delay(Constants.DELAY)
        data.postValue(handlePageResponse())
    }

    private suspend fun handlePageResponse() : Resource<Country>{

        val resultEntry = mDb?.currencyCalculatorDao()?.loadAllCountries()
        if (resultEntry != null) {
            val symbols = mutableMapOf<String,String>();
            resultEntry.forEach { entry ->
                symbols[entry.getSymbol()] = entry.getName()
            }
            val country = Country(success = true, symbols = symbols)
            return Resource.Success("Success",country)
        }
        val response = RetrofitInstance.api.getCountries()
        if (response.isSuccessful) {
            response.body()?.let {
                it.symbols.forEach { item ->
                    val countryEntry = CountryEntry(symbol = item.key, name = item.value)
                    mDb?.currencyCalculatorDao()?.insertCountry(countryEntry)
                }
                return Resource.Success("Success", it)
            }
        }
        val msg = response.message()
        return Resource.Error(msg)
    }

}