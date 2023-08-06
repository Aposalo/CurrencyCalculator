package aposalo.com.currencycalculator.domain.repository

import androidx.lifecycle.MutableLiveData
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.local.countries.CountryEntry
import aposalo.com.currencycalculator.domain.server.api.authentication.ApiInstance
import aposalo.com.currencycalculator.domain.server.dto.Country
import aposalo.com.currencycalculator.utils.Constants.Companion.API_EXCEEDED_CALLS_CODE
import aposalo.com.currencycalculator.utils.Resource
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
                val symbols = mutableMapOf<String, String>()
                resultEntry.forEach {
                    symbols[it.symbol] = it.name
                }
                val country = Country(
                    currencies = symbols
                )
                return Resource.Success("Success", country)
            }
            val response = ApiInstance.api.getCountries()//an argei to api na epikoinwnhsei na pairnw ena mhjnuma sentry
            val code = response.code()
            if(code == API_EXCEEDED_CALLS_CODE)
            {
                Sentry.captureMessage("API exceeded calls, please change key")
                return Resource.Error("API exceeded calls, please change key")
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
        catch (e : Exception) {
            e.message?.let {
                Sentry.captureMessage(it)
                return Resource.Error(it)
            }
        }
        return Resource.Error("Error")
    }


}