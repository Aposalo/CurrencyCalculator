package aposalo.com.currencycalculator.domain.local.currency

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "currency_calculator")
class CurrencyCalculatorEntry(
    @PrimaryKey(autoGenerate = true) private var id: Int = 0,
    @ColumnInfo("currency_to") private var to: String,
    @ColumnInfo("currency_from") private var from: String,
    private var amount: String,
    private var result: String,
    private var count: Int = 1) {

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

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

    fun getCount(): Int {
        return count
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun increaseCount(){
        count++
    }

    fun getCurrencyEntry() : String {
        return Json.encodeToString(CurrencyResultSerializable(to, from, amount, result))
    }
}