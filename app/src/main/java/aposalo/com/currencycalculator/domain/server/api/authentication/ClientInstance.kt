package aposalo.com.currencycalculator.domain.server.api.authentication

object ApiInstance {

        private val longRetrofitInstance = RetrofitInstance(60)
        private val shortRetrofitInstance = RetrofitInstance(5)

        val longApi = longRetrofitInstance.api
        val shortApi = shortRetrofitInstance.api

}