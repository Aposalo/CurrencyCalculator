package aposalo.com.currencycalculator.api.authentication

import aposalo.com.currencycalculator.util.Constants.Companion.AUTHORIZATION_TYPE
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val value: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header(AUTHORIZATION_TYPE, value)
            .build()
        return chain.proceed(authenticatedRequest)
    }
}