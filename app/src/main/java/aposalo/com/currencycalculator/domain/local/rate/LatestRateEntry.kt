package aposalo.com.currencycalculator.domain.local.rate

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "latest_rate", primaryKeys = ["currency_to","currency_from"])
class LatestRateEntry (@ColumnInfo("currency_to") private var to: String,
                       @ColumnInfo("currency_from") private var from: String,
                       private var rate: Float,
                       @ColumnInfo("latest_date") private var latestDate: Long){

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

    fun getRate(): Float {
        return rate
    }

    fun setRate(rate: Float) {
        this.rate = rate
    }

    fun getLatestDate(): Long {
        return latestDate
    }

    fun setLatestDate(latestDate: Long) {
        this.latestDate = latestDate
    }

}