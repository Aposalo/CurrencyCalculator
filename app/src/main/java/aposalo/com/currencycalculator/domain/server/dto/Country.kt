package aposalo.com.currencycalculator.domain.server.dto

data class Country(
    val success: Boolean,
    val symbols: Map<String, String>
)