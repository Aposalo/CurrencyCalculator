package aposalo.com.currencycalculator.domain.server.dto

data class Country(
    val success: Boolean = true,
    val currencies: Map<String, String>
)