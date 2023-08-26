package aposalo.com.currencycalculator.domain.server.api.authentication

import aposalo.com.currencycalculator.domain.server.api.CurrencyCalculatorApi
import aposalo.com.currencycalculator.utils.AUTHORIZATION_TYPE
import aposalo.com.currencycalculator.utils.CLIENT_BASE_URL
import aposalo.com.currencycalculator.utils.LONG_TIMEOUT
import aposalo.com.currencycalculator.utils.TOKEN
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiInstance {
        private val retrofitInstance = RetrofitInstance()
        val api = retrofitInstance.api
}

class RetrofitInstance(private var timeout : Long = LONG_TIMEOUT) {

        private val client : OkHttpClient by lazy {
                OkHttpClient.Builder()
                        .readTimeout(timeout, TimeUnit.SECONDS)
                        .connectTimeout(timeout, TimeUnit.SECONDS)
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

        inner class AuthInterceptor(private val value: String) : Interceptor {

                override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request()
                        val authenticatedRequest = request.newBuilder()
                                .header(AUTHORIZATION_TYPE, value)
                                .build()
                        return chain.proceed(authenticatedRequest)
                }
        }
}