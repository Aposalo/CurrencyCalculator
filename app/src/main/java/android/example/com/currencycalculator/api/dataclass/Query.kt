package android.example.com.currencycalculator.api.dataclass

data class Query(
    val amount: Float,
    val from: String,
    val to: String
)