package aposalo.com.currencycalculator.domain.server.api

import aposalo.com.currencycalculator.domain.server.dto.Country
import aposalo.com.currencycalculator.domain.server.dto.FixerDto
import aposalo.com.currencycalculator.domain.server.dto.Rate
import aposalo.com.currencycalculator.utils.Constants.Companion.CONVERT_ANNOTATION
import aposalo.com.currencycalculator.utils.Constants.Companion.LATEST_ANNOTATION
import aposalo.com.currencycalculator.utils.Constants.Companion.SYMBOLS_ANNOTATION
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyCalculatorApi {

    @GET(CONVERT_ANNOTATION)
    suspend fun getFixerConvert(@Query("to") to: String, @Query("from") from: String, @Query("amount") amount: String): Response<FixerDto>

    @GET(SYMBOLS_ANNOTATION)
    suspend fun getCountries(): Response<Country>

    @GET(LATEST_ANNOTATION)
    suspend fun getLatestRates(@Query("source") source: String) : Response<Rate>
}