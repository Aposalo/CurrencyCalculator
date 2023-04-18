package aposalo.com.currencycalculator.domain.local.currency

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResultSerializable(val to: String, val from: String, val amount: String, val result : String)
