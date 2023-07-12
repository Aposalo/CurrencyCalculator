package aposalo.com.currencycalculator.domain.local.currency

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Entity(tableName = "currency_calculator", primaryKeys = ["currency_to","currency_from","amount"])
@Parcelize
data class CurrencyCalculatorEntry(
    @ColumnInfo("currency_from") var from: String,
    @ColumnInfo("currency_to") var to: String,
    var amount: String,
    var result: String,
    @ColumnInfo("latest_date") var latestDate: Long) : Parcelable