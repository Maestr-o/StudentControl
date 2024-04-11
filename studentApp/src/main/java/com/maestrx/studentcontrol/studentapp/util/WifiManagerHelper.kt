package com.maestrx.studentcontrol.studentapp.util

import android.content.Context
import android.net.wifi.WifiManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val wifi: WifiManager? by lazy {
        context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    }

    fun getSSID(): String? {
        val wifiInfo = wifi?.connectionInfo
        return wifiInfo?.ssid?.replace("\"", "")
    }

    fun disconnect() {
        wifi?.disconnect()
    }
}