package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor() : ViewModel() {

    private var state = mutableStateOf<ControlStatus>(ControlStatus.WifiIsDown)

    private val _wifiResults = mutableStateListOf<ScanResult>()
    val wifiResults: List<ScanResult> = _wifiResults
    private lateinit var wifiManager: WifiManager
    private lateinit var handler: Handler
    private val scanInterval: Long = 10000

    var selectedNetwork = mutableStateOf<ScanResult?>(null)

    fun onEvent(event: ControlEvent) {
        when (event) {
            is ControlEvent.SetScreenStatus -> {
                changeScreenStatus(event.status)
            }
            is ControlEvent.SelectNetwork -> {
                selectNetwork(event.network)
            }
        }
    }

    private fun changeScreenStatus(status: ControlStatus) {
        state.value = status
    }

    private fun selectNetwork(network: ScanResult?) {
        selectedNetwork.value = network
    }

    fun startWifiScan(context: Context) {
        wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        handler = Handler(Looper.getMainLooper())
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)

        viewModelScope.launch {
            while (true) {
                val success = wifiManager.startScan()
                if (!success) {
                    scanFailure()
                }
                delay(scanInterval)
            }
        }
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    private fun scanSuccess() {
        _wifiResults.clear()
        val result = wifiManager.scanResults.filter {
            it.SSID.isNotBlank()
        }
        _wifiResults.addAll(result)
    }

    private fun scanFailure() {
        _wifiResults.clear()
        val result = wifiManager.scanResults.filter {
            it.SSID.isNotBlank()
        }
        _wifiResults.addAll(result)
    }
}