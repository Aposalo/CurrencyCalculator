package aposalo.com.currencycalculator.domain.server.dto

data class Rate(
    val source: String,
    val quotes: Map <String, Float>,
    val success: Boolean,
    val timestamp: Long
)