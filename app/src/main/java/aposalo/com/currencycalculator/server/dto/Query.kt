package aposalo.com.currencycalculator.server.dto

data class Query(
    val amount: Float,
    val from: String,
    val to: String
)