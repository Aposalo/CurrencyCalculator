package aposalo.com.currencycalculator.domain.local

import androidx.room.*

@Dao
interface CurrencyCalculatorDao {

    @Insert
    suspend fun insertCurrency(currencyCalculatorEntry: CurrencyCalculatorEntry)

    @Insert
    suspend fun insertCountry(countryEntry: CountryEntry)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCurrency(currencyCalculatorEntry: CurrencyCalculatorEntry)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCountry(countryEntry: CountryEntry)

    @Delete
    suspend fun deleteCurrency(currencyCalculatorEntry: CurrencyCalculatorEntry)

    @Delete
    suspend fun deleteCountry(countryEntry: CountryEntry)

    @Query("DELETE FROM currency_calculator")
    suspend fun clearDatabase()

    @Query("DELETE FROM country")
    suspend fun clearCountry()

    @Query("SELECT * FROM currency_calculator ORDER BY count ASC")
    suspend fun loadAllCurrencies(): List<CurrencyCalculatorEntry>?

    @Query("SELECT DISTINCT * FROM country where name LIKE '%' || :word || '%' or symbol LIKE '%' || :word || '%' ORDER BY name ASC")
    suspend fun loadAllCurrencies(word: String): List<CurrencyCalculatorEntry>?

    @Query("SELECT * FROM country ORDER BY name ASC")
    suspend fun loadAllCountries(): List<CountryEntry>?

    @Query("SELECT * FROM currency_calculator WHERE" +
            " currency_to LIKE :to AND currency_from LIKE :from AND amount LIKE :amount LIMIT 1")//
    suspend fun getResult(to: String, from: String, amount: String): CurrencyCalculatorEntry

    @Query("SELECT result FROM currency_calculator WHERE " +
            "id = :id")
    suspend fun getCurrency(id: Int): String
}