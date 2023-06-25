package aposalo.com.currencycalculator.domain.local.currency

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "currency_calculator", primaryKeys = ["currency_to","currency_from","amount"])
class CurrencyCalculatorEntry(
    @ColumnInfo("currency_from") private var from: String,
    @ColumnInfo("currency_to") private var to: String,
    private var amount: String,
    private var result: String,
    @ColumnInfo("latest_date") private var latestDate: Long) {

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

    fun getLatestDate(): Long {
        return latestDate
    }

    fun setLatestDate(latestDate: Long) {
        this.latestDate = latestDate
    }
}