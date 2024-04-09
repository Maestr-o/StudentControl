package com.maestrx.studentcontrol.teacherapp.ap

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log

object APUtils {

    fun isWifiApEnabled(context: Context): Boolean {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        try {
            val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            return method.invoke(wifiManager) as Boolean
        } catch (e: Exception) {
            Log.e("AP", "Error: $e")
        }
        return false
    }

    fun goToAPSettings(context: Context) {
        val intent = Intent().apply {
            component = ComponentName(
                "com.android.settings",
                "com.android.settings.Settings\$TetherSettingsActivity"
            )
        }
        context.startActivity(intent)
    }
}