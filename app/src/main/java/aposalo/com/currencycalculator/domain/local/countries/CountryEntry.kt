package aposalo.com.currencycalculator.domain.local.countries

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "country")
@Parcelize
data class CountryEntry(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var symbol: String,
) : Parcelable