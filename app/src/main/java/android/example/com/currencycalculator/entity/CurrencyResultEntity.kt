package android.example.com.currencycalculator.entity

import android.example.com.currencycalculator.model.CurrencyResultModel
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyResultEntity (
    @PrimaryKey val id : Int? = null,
    val amount: Float?,
    val currency: Float,
    val from: String,
    val to: String,
    val count: Int)
{

    fun toCurrencyResultModel() : CurrencyResultModel{
        return CurrencyResultModel(
            amount = amount,
            currency = currency,
            from = from,
            to = to
        )
    }


}