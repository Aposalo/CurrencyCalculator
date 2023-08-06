package aposalo.com.currencycalculator.domain.server.api.authentication

import aposalo.com.currencycalculator.utils.Constants.Companion.LONG_TIMEOUT

object ApiInstance {
        private val longRetrofitInstance = RetrofitInstance(LONG_TIMEOUT)
        val api = longRetrofitInstance.api
}