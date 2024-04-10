package com.maestrx.studentcontrol.studentapp.util

import android.content.Context
import android.net.wifi.WifiManager

object WifiService {

    fun isWifiEnabled(context: Context): Boolean {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }
}