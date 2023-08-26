package aposalo.com.currencycalculator.domain.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.repository.CurrencyCalculatorRepository
import aposalo.com.currencycalculator.domain.server.dto.FixerDto
import aposalo.com.currencycalculator.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CurrencyCalculatorModel(mDb: AppDatabase?) : ViewModel() {

    private val currencyCalculatorRepository: CurrencyCalculatorRepository = CurrencyCalculatorRepository(mDb)


    private val _dataCurrencyCalculatorFlow = MutableStateFlow<Resource<FixerDto>>(Resource.Success(null))
    val dataCurrencyCalculator = _dataCurrencyCalculatorFlow.asStateFlow()

    private var latestFrom = String()
    private var latestTo  = String()
    private var latestAmount  = 0.0f

    fun getUserPage(from: String, to: String,  amount: Float) {
        viewModelScope.launch {
            _dataCurrencyCalculatorFlow.emit(Resource.Loading())
            latestFrom = from
            latestTo = to
            latestAmount = amount
            _dataCurrencyCalculatorFlow.emit(currencyCalculatorRepository.handlePageResponse(latestAmount, latestFrom,latestTo))
        }
    }

}
