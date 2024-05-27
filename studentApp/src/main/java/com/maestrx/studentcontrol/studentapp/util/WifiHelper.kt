package com.maestrx.studentcontrol.studentapp.util

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import com.maestrx.studentcontrol.studentapp.R
import java.net.InetAddress

object WifiHelper {

    fun getCurrentSSID(context: Context): String {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            return context.getString(R.string.wifi_network)
        }
        return try {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            if (wifiInfo.ssid != null && wifiInfo.ssid != Constants.UNKNOWN_SSID) {
                val ssid = wifiInfo.ssid
                if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid.substring(1, ssid.length - 1)
                } else {
                    ssid
                }
            } else {
                context.getString(R.string.wifi_network)
            }
        } catch (e: Exception) {
            context.getString(R.string.wifi_network)
        }
    }

    fun getServerAddress(context: Context): InetAddress {
        val wm: WifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
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