package android.example.com.currencycalculator.repository.dto

import android.example.com.currencycalculator.model.CurrencyResultModel

data class FixerDto(
    val date: String,
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
){
    fun toCurrencyResultModel() : CurrencyResultModel{
        return CurrencyResultModel(
            amount = query.amount,
            currency = result.toFloat(),
            from = query.from,
            to = query.to
        )
    }
}