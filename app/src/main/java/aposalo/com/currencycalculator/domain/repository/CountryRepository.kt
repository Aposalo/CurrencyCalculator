package aposalo.com.currencycalculator.domain.repository

import androidx.lifecycle.MutableLiveData
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.countries.CountryEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.ApiInstance
import aposalo.com.currencycalculator.domain.server.dto.Country
import aposalo.com.currencycalculator.util.Constants.Companion.API_EXCEEDED_CALLS_CODE
import aposalo.com.currencycalculator.util.Resource
import io.sentry.Sentry

class CountryRepository(private val mDb : AppDatabase?) {

    val data = MutableLiveData<Resource<Country>>()

    suspend fun getCountries(word : String) {
        data.postValue(handlePageResponse(word))
    }

    private suspend fun handlePageResponse(word : String) : Resource<Country> {
        try {
            val resultEntry = if (word.isNotEmpty()) {
                mDb?.countryDao()?.loadAllCountries(word = word)
            } else {
                mDb?.countryDao()?.loadAllCountries()
            }

            if (!resultEntry.isNullOrEmpty()) {
                val symbols = mutableMapOf<String, String>();
                resultEntry.forEach { entry ->
                    symbols[entry.getSymbol()] = entry.getName()
                }
                val country = Country(
                    success = true,
                    currencies = symbols
                )

                return Resource.Success("Success", country)
            }
            val response = ApiInstance.longApi.getCountries()
            val code = response.code()
            if(code == API_EXCEEDED_CALLS_CODE)
            {
                Sentry.captureMessage("API exceeded calls, please change key")
                return Resource.Error("Error")
            }
            if (response.isSuccessful) {
                response.body()?.let {
                    it.currencies.forEach { item ->
                        val countryEntry = CountryEntry (
                            symbol = item.key,
                            name = item.value
                        )
                        mDb?.countryDao()?.insertCountry(countryEntry)
                    }
                    return Resource.Success("Success", it)
                }
            }
        }
        catch (e : Exception){
            e.message?.let { Sentry.captureMessage(it) }
        }
        return Resource.Error("Error")
    }


}