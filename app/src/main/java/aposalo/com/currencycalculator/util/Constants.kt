package aposalo.com.currencycalculator.util

class Constants {
    companion object {

        private const val LIBRARY = "fixer"
        const val TOKEN = "cPiGHcsmJAfkBuJ1vRwnV0YV9YaKd4g5"

        const val CONVERT_ANNOTATION = "$LIBRARY/convert"
        const val SYMBOLS_ANNOTATION = "$LIBRARY/symbols"
        const val AUTHORIZATION_TYPE = "apikey"
        const val CLIENT_BASE_URL = "https://api.apilayer.com/"
        const val DELAY: Long = 600
        const val CURRENCY_CHANGE = "currency"
        const val SHARED_PREF = "currency_calculator"
        const val DATABASE_NAME = "CurrencyCalculator"

        const val CURRENCY_TEXT_LABEL = "currencySpinner"
        const val RESULT_TEXT_LABEL = "resultSpinner"
        const val SOLUTION_TEXT_LABEL = "solutionTv"
        const val RESULT_TEXT_VALUE_LABEL = "resultTv"
    }
}