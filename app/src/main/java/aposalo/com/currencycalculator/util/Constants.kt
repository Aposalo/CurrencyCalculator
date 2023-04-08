package aposalo.com.currencycalculator.util

class Constants {
    companion object {

        private const val LIBRARY = "fixer"
        const val TOKEN = "zyN6PbHkEdPmSIRgANJIk50Rv2yfmaPO"

        const val CONVERT_ANNOTATION = "$LIBRARY/convert"
        const val SYMBOLS_ANNOTATION = "$LIBRARY/symbols"
        const val AUTHORIZATION_TYPE = "apikey"
        const val CLIENT_BASE_URL = "https://api.apilayer.com/"
        const val DELAY: Long = 600
    }
}