package android.example.com.currencycalculator.repository.dto

data class FixerDto(
    val date: String,
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
)