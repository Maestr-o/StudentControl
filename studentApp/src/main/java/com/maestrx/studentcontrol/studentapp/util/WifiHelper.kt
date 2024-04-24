package com.maestrx.studentcontrol.studentapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import java.net.InetAddress

object WifiHelper {

    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    fun getServerAddress(context: Context): InetAddress {
        val wm: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val address = InetAddress.getByAddress(intToByteArray(wm.dhcpInfo.gateway))
        Log.d(Constants.DEBUG_TAG, "Server address: $address")
        return address
    }

    private fun intToByteArray(value: Int): ByteArray {
        val byteBuffer = ByteArray(4)
        byteBuffer[0] = (value and 0xFF).toByte()
        byteBuffer[1] = (value shr 8 and 0xFF).toByte()
        byteBuffer[2] = (value shr 16 and 0xFF).toByte()
        byteBuffer[3] = (value shr 24 and 0xFF).toByte()
        return byteBuffer
    }
}