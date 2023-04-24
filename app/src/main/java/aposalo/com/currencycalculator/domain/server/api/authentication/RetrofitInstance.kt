package aposalo.com.currencycalculator.domain.server.api.authentication

import aposalo.com.currencycalculator.domain.server.api.CurrencyCalculatorApi
import aposalo.com.currencycalculator.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance(private var timeout : Long = 0) {

        private val client : OkHttpClient by lazy {
            OkHttpClient.Builder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(Constants.TOKEN))
                .build()
        }

        val api : CurrencyCalculatorApi by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.CLIENT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(CurrencyCalculatorApi::class.java)
        }
}