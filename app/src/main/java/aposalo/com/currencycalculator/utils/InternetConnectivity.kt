package aposalo.com.currencycalculator.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
Checks if there is Internet Connectivity
**/
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?: return false
    val transportCellularCapabilities = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    val transportWifiCapabilities = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    val transportEthernetCapabilities = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    return transportCellularCapabilities
            || transportWifiCapabilities
            || transportEthernetCapabilities
}
