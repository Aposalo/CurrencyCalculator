package aposalo.com.currencycalculator.domain.local.rate

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Entity(tableName = "latest_rate", primaryKeys = ["currency_to","currency_from"])
@Parcelize
data class LatestRateEntry (
    @ColumnInfo("currency_to") var to: String,
    @ColumnInfo("currency_from") var from: String,
    var rate: Float,
    @ColumnInfo("latest_date") var latestDate: Long): Parcelable