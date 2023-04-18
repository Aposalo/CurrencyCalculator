package aposalo.com.currencycalculator.domain.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.repository.CountryRepository
import kotlinx.coroutines.launch

class CountryModel(private val mDb: AppDatabase?): ViewModel() {

    val countriesRepository: CountryRepository = CountryRepository(mDb)

    fun getCountries(){
        viewModelScope.launch {
            countriesRepository.getCountries()
        }
    }

    fun getCountries(word: String){
        viewModelScope.launch {
            countriesRepository.getCountries(word)
        }
    }
}