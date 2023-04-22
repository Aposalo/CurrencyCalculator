package aposalo.com.currencycalculator.domain.local.currency

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "currency_calculator")
class CurrencyCalculatorEntry(
    @PrimaryKey(autoGenerate = false) @ColumnInfo("currency_to") private var to: String,
    @PrimaryKey(autoGenerate = false) @ColumnInfo("currency_from") private var from: String,
    @PrimaryKey(autoGenerate = false) private var amount: String,
    private var result: String,
    @ColumnInfo("latest_date") private var latestDate: Date) {

    fun getTo(): String {
        return to
    }

    fun setTo(to: String) {
        this.to = to
    }

    fun getFrom(): String {
        return from
    }

    fun setFrom(from: String) {
        this.from = from
    }

    fun getAmount(): String {
        return amount
    }

    fun setAmount(amount: String) {
        this.amount = amount
    }

    fun getResult(): String {
        return result
    }

    fun setResult(result: String) {
        this.result = result
    }
}