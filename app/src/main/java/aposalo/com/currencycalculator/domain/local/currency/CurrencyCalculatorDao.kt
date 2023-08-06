package aposalo.com.currencycalculator.domain.local.currency

import androidx.room.*

@Dao
interface CurrencyCalculatorDao {

    @Insert
    suspend fun insertCurrency(currencyCalculatorEntry: CurrencyCalculatorEntry)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCurrency(currencyCalculatorEntry: CurrencyCalculatorEntry)

    @Delete
    suspend fun deleteCurrency(currencyCalculatorEntry: CurrencyCalculatorEntry)

    @Query("DELETE FROM currency_calculator")
    suspend fun clearCurrencies()

    @Query("SELECT * FROM currency_calculator WHERE" +
            " currency_to LIKE :to AND currency_from LIKE :from AND amount LIKE :amount LIMIT 1")
    suspend fun getResult(to: String, from: String, amount: String): CurrencyCalculatorEntry?

}