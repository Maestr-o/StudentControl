package com.maestrx.studentcontrol.studentapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.MacAddress
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkCallbackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    fun registerNetworkCallback(
        ssid: String,
        bssid: String,
        password: String? = null,
        onNetworkAvailable: () -> Unit,
        onNetworkUnavailable: () -> Unit
    ) {
        val wifiNetworkSpecifier = if (password != null) {
            WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setBssid(MacAddress.fromString(bssid))
                .setWpa2Passphrase(password)
                .build()
        } else {
            WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .setBssid(MacAddress.fromString(bssid))
                .build()
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .build()

        val handler = Handler(Looper.getMainLooper())
        var attempt = 0
        val baseDelay = Constants.WIFI_TIMEOUT_DEFAULT

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                connectivityManager.bindProcessToNetwork(network)
                onNetworkAvailable()
            }

            override fun onUnavailable() {
                super.onUnavailable()
                attempt++
                val delay = baseDelay * attempt
                handler.postDelayed({
                    connectivityManager.requestNetwork(networkRequest, this)
                }, delay)
                onNetworkUnavailable()
            }
        }

        connectivityManager.requestNetwork(networkRequest, networkCallback!!)
    }

    fun unregisterNetworkCallback() {
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
        }
    }
}