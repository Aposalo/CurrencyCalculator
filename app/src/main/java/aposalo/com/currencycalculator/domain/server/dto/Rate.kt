package aposalo.com.currencycalculator.domain.server.dto

import java.util.Date

data class Rate(
    val base: String,
    val date: Date,
    val rates: Map <String, Float>,
    val success: Boolean,
    val timestamp: Int
)