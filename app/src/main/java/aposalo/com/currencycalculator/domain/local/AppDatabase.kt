package aposalo.com.currencycalculator.domain.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import aposalo.com.currencycalculator.domain.local.countries.CountryDao
import aposalo.com.currencycalculator.domain.local.countries.CountryEntry
import aposalo.com.currencycalculator.domain.local.currency.CurrencyCalculatorDao
import aposalo.com.currencycalculator.domain.local.currency.CurrencyCalculatorEntry
import aposalo.com.currencycalculator.domain.local.rate.LatestRateEntry
import aposalo.com.currencycalculator.domain.local.rate.LatestRateDao
import aposalo.com.currencycalculator.utils.Constants.Companion.DATABASE_NAME

@Database(entities = [CurrencyCalculatorEntry::class, CountryEntry::class, LatestRateEntry::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private val LOG_TAG = AppDatabase::class.java.simpleName
        private val LOCK = Any()
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    Log.d(LOG_TAG, "Creating new database instance")
                    sInstance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            Log.d(LOG_TAG, "Getting the database instance")
            return sInstance
        }
    }

    abstract fun currencyCalculatorDao(): CurrencyCalculatorDao

    abstract fun countryDao(): CountryDao

    abstract fun latestRateDao(): LatestRateDao

}