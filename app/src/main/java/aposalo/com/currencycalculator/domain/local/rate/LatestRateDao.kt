package aposalo.com.currencycalculator.domain.local.rate

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LatestRateDao {

    @Insert
    suspend fun insertLatestRate(latestRate : LatestRateEntry)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLatestRate(latestRate : LatestRateEntry)

    @Delete
    suspend fun deleteLatestRate(latestRate : LatestRateEntry)

    @Query("DELETE FROM latest_rate")
    suspend fun clearCurrencies()

    @Query("SELECT * FROM latest_rate WHERE" +
            " currency_to LIKE :to AND currency_from LIKE :from LIMIT 1")
    suspend fun getResult(to: String, from: String): LatestRateEntry
}