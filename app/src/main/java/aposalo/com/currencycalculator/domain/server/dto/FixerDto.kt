package aposalo.com.currencycalculator.domain.server.dto

import java.util.Date

data class FixerDto(
    val date: Date,
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
)