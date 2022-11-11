package android.example.com.currencycalculator.model

data class CurrencyResultModel(
    val amount: Float?,
    val currency: Float,
    val from: String,
    val to: String
) {
}