package aposalo.com.currencycalculator.domain.local

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResultSerializable(val to: String, val from: String, val amount: String, val result : String)
