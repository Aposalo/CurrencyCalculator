package aposalo.com.currencycalculator.domain.local.rate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "latest_rate")
class LatestRateEntry (@PrimaryKey(autoGenerate = false) @ColumnInfo("currency_to") private var to: String,
                       @PrimaryKey(autoGenerate = false) @ColumnInfo("currency_from") private var from: String,
                       private var rate: Float,
                       @ColumnInfo("latest_date") private var latestDate: Date){

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

    fun getLatestDate(): Date {
        return latestDate
    }

    fun setLatestDate(latestDate: Date) {
        this.latestDate = latestDate
    }

}