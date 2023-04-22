package aposalo.com.currencycalculator.domain.local.countries

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CountryDao {

    @Insert
    suspend fun insertCountry(countryEntry: CountryEntry)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCountry(countryEntry: CountryEntry)

    @Delete
    suspend fun deleteCountry(countryEntry: CountryEntry)

    @Query("DELETE FROM country")
    suspend fun clearCountry()

    @Query("SELECT DISTINCT * FROM country where name LIKE '%' || :word || '%' or symbol LIKE '%' || :word || '%' ORDER BY name ASC")
    suspend fun loadAllCountries(word: String): List<CountryEntry>?

    @Query("SELECT * FROM country ORDER BY name ASC")
    suspend fun loadAllCountries(): List<CountryEntry>?
}