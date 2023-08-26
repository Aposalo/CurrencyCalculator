package aposalo.com.currencycalculator.utils

import androidx.room.TypeConverter
import java.util.Date

@TypeConverter
fun toDate(dateLong: Long?): Date? {
    return dateLong?.let { Date(it) }
}

@TypeConverter
fun fromDate(date: Date?): Long? {
    return date?.time
}