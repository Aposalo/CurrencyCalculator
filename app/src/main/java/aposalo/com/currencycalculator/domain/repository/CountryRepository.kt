package aposalo.com.currencycalculator.domain.repository

import androidx.lifecycle.MutableLiveData
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.countries.CountryEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.RetrofitInstance
import aposalo.com.currencycalculator.domain.server.dto.Country
import aposalo.com.currencycalculator.util.Constants
import aposalo.com.currencycalculator.util.Resource
import io.sentry.Sentry
import kotlinx.coroutines.delay

class CountryRepository(private val mDb : AppDatabase?) {

    val data : MutableLiveData<Resource<Country>> = MutableLiveData()

    suspend fun getCountries() {
        data.postValue(Resource.Loading())
        delay(Constants.DELAY)
        data.postValue(handlePageResponse())
    }

    suspend fun getCountries(word : String) {
        data.postValue(Resource.Loading())
        delay(Constants.DELAY)
        data.postValue(handlePageResponse(word))
    }

    private suspend fun handlePageResponse(word : String) : Resource<Country> {
        try {
            val response = handlePageResponse()
            if (response.message == "Error"){
                return Resource.Error("Error")
            }
            val resultEntry = mDb?.currencyCalculatorDao()?.loadAllCountries(word = word)
            val symbols = mutableMapOf<String,String>();
            resultEntry?.forEach { entry ->
                symbols[entry.getSymbol()] = entry.getName()
            }
            val country = Country(success = true, symbols = symbols)
            return Resource.Success("Success",country)
        }
        catch (e : Exception){
            e.message?.let { Sentry.captureMessage(it) }
        }
        return Resource.Error("Error")
    }

    private suspend fun handlePageResponse() : Resource<Country> {
        try {
            val resultEntry = mDb?.currencyCalculatorDao()?.loadAllCountries()
            if (!resultEntry.isNullOrEmpty()) {
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
        }
        catch (e : Exception) {
            e.message?.let { Sentry.captureMessage(it) }
        }
        return Resource.Error("Error")
    }

}