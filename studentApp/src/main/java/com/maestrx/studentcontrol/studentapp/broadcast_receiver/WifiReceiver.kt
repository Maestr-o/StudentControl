package com.maestrx.studentcontrol.studentapp.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager

class WifiReceiver(private val onWifiEnabled: (Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
            onWifiEnabled(isWifiEnabled(context))
        }
    }

    companion object {
        fun isWifiEnabled(context: Context): Boolean =
            (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .isWifiEnabled
    }
}