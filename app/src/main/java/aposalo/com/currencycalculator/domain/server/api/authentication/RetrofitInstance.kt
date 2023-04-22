package aposalo.com.currencycalculator.domain.server.api.authentication

import aposalo.com.currencycalculator.domain.server.api.CurrencyCalculatorApi
import aposalo.com.currencycalculator.util.Constants.Companion.CLIENT_BASE_URL
import aposalo.com.currencycalculator.util.Constants.Companion.TOKEN
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val lateClient : OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor(TOKEN))
        .build()
    }

    val lateApi : CurrencyCalculatorApi by lazy {
        Retrofit.Builder()
            .baseUrl(CLIENT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(lateClient)
            .build()
            .create(CurrencyCalculatorApi::class.java)
    }

    private val client : OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.MILLISECONDS)
            .connectTimeout(60, TimeUnit.MILLISECONDS)
            .addInterceptor(AuthInterceptor(TOKEN))
            .build()
    }

    val api : CurrencyCalculatorApi by lazy {
        Retrofit.Builder()
            .baseUrl(CLIENT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CurrencyCalculatorApi::class.java)
    }
}