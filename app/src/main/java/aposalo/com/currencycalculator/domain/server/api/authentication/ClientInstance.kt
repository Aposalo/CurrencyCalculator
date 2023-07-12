package aposalo.com.currencycalculator.domain.server.api.authentication

import aposalo.com.currencycalculator.util.Constants.Companion.LONG_TIMEOUT
import aposalo.com.currencycalculator.util.Constants.Companion.SHORT_TIMEOUT

object ApiInstance {

        private val longRetrofitInstance = RetrofitInstance(LONG_TIMEOUT)
        private val shortRetrofitInstance = RetrofitInstance(SHORT_TIMEOUT)

        val longApi = longRetrofitInstance.api
        val shortApi = shortRetrofitInstance.api

}