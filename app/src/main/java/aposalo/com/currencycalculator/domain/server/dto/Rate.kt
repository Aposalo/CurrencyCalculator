package aposalo.com.currencycalculator.domain.server.dto

data class Rate(
    val base: String,
    val date: String,
    val rates: Map <String, Float>,
    val success: Boolean,
    val timestamp: Int
)