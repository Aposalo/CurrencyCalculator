package android.example.com.currencycalculator.api.dataclass

data class Fixer(
    val date: String,
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
)