package aposalo.com.currencycalculator.server.api.authentication

import aposalo.com.currencycalculator.server.api.CurrencyCalculatorApi
import aposalo.com.currencycalculator.util.Constants.Companion.CLIENT_BASE_URL
import aposalo.com.currencycalculator.util.Constants.Companion.TOKEN
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val client : OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor(TOKEN))
        .build()
    }

    val api: CurrencyCalculatorApi by lazy {
        Retrofit.Builder()
            .baseUrl(CLIENT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CurrencyCalculatorApi::class.java)
    }
}