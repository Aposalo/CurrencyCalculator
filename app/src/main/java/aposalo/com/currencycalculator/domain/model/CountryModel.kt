package aposalo.com.currencycalculator.domain.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.repository.CountryRepository
import kotlinx.coroutines.launch

class CountryModel(mDb: AppDatabase?): ViewModel() {

    val countriesRepository: CountryRepository = CountryRepository(mDb)

    fun getCountries(word: String = String()) {
        viewModelScope.launch {
            countriesRepository.getCountries(word)
        }
    }
}