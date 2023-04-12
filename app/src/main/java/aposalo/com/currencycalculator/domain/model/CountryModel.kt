package aposalo.com.currencycalculator.domain.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aposalo.com.currencycalculator.domain.repository.CountryRepository
import kotlinx.coroutines.launch

class CountryModel: ViewModel() {

    val countriesRepository: CountryRepository = CountryRepository()

    fun getCountries(){
        viewModelScope.launch {
            countriesRepository.getCountries()
        }
    }
}