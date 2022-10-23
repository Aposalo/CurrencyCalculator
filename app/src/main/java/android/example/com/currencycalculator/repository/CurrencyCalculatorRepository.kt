package android.example.com.currencycalculator.repository

import android.example.com.currencycalculator.api.authentication.RetrofitInstance

class CurrencyCalculatorRepository() {

    suspend fun getFixerConvert(to: String, from: String, amount: Int) = RetrofitInstance.api.getFixerConvert(to, from, amount)

}