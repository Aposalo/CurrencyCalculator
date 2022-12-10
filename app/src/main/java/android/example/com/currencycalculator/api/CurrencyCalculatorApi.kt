package android.example.com.currencycalculator.api

import android.example.com.currencycalculator.repository.dto.FixerDto
import android.example.com.currencycalculator.util.Constants.Companion.CONVERT_ANNOTATION
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyCalculatorApi {

    @GET(CONVERT_ANNOTATION)
    suspend fun getFixerConvert(@Query("to") to: String, @Query("from") from: String, @Query("amount") amount: Float): Response<FixerDto>
}