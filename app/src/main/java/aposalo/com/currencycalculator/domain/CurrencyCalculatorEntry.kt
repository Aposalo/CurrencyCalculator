package aposalo.com.currencycalculator.domain

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "currency_calculator")
class CurrencyCalculatorEntry {
    @PrimaryKey(autoGenerate = true)
    private var id = 0
    private lateinit var to: String
    private lateinit var from: String
    private lateinit var amount: String
    private lateinit var result: String
    private var count = 1

    @Ignore
    fun CurrencyCalculatorEntry(to: String, from: String, amount: String, result: String) {
        this.to = to
        this.from = from
        this.amount = amount
        this.result = result
    }

    fun CurrencyCalculatorEntry(id: Int,to: String, from: String, amount: String, result: String, count: Int) {
        this.id = id
        this.to = to
        this.from = from
        this.amount = amount
        this.result = result
        this.count = count
    }

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    //fun
}