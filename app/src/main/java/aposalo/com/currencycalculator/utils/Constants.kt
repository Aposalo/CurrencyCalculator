package aposalo.com.currencycalculator.utils

import aposalo.com.currencycalculator.BuildConfig

private const val LIBRARY = "currency_data"
const val TOKEN = BuildConfig.API_KEY

const val CONVERT_ANNOTATION = "$LIBRARY/convert"
const val SYMBOLS_ANNOTATION = "$LIBRARY/list"
const val LATEST_ANNOTATION = "$LIBRARY/live"
const val AUTHORIZATION_TYPE = "apikey"
const val CLIENT_BASE_URL = BuildConfig.CLIENT_BASE_URL
const val DELAY: Long = 600
const val CURRENCY_CHANGE = "currency"
const val SHARED_PREF = "currency_calculator"
const val DATABASE_NAME = "CurrencyCalculator"
const val API_EXCEEDED_CALLS_CODE = 429//Code for too many requests
const val LONG_TIMEOUT: Long = 60

const val CURRENCY_TEXT_LABEL = "currencySpinner"
const val RESULT_TEXT_LABEL = "resultSpinner"
const val SOLUTION_TEXT_LABEL = "solutionTv"
const val RESULT_TEXT_VALUE_LABEL = "resultTv"

const val RATE_WORKER = "rate_worker"
