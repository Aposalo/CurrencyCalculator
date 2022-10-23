package android.example.com.currencycalculator.api.dataclass

data class Query(
    val amount: Int,
    val from: String,
    val to: String
)