package com.maestrx.studentcontrol.studentapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlViewModel
import javax.inject.Inject

class WifiStateReceiver @Inject constructor(
    private val controlViewModel: ControlViewModel,
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
            val wifiStateExtra =
                intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
            when (wifiStateExtra) {
                WifiManager.WIFI_STATE_ENABLED -> {
                    controlViewModel.changeWifiModuleState(true)
                }

                WifiManager.WIFI_STATE_DISABLED -> {
                    controlViewModel.changeWifiModuleState(false)
                }
            }
        }
    }
}