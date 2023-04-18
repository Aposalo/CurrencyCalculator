package aposalo.com.currencycalculator.domain.local.countries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country")
class CountryEntry(
    @PrimaryKey(autoGenerate = true) private var id: Int = 0,
    private var name: String,
    private var symbol: String,
) {

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getSymbol(): String {
        return symbol
    }

    fun setSymbol(symbol: String) {
        this.symbol = symbol
    }
}