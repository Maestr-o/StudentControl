package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.MacAddress
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.studentapp.util.Constants
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
    private val scanInterval: Long = 15000

    var selectedNetwork = mutableStateOf<ScanResult?>(null)
    var connectedNetwork = mutableStateOf<String?>(null)
    var connecting = mutableStateOf(false)
    var stopConnecting = mutableStateOf(false)
    var checkIn = mutableStateOf(false)

    fun onEvent(event: ControlEvent) {
        when (event) {
            is ControlEvent.SetScreenStatus -> {
                changeScreenStatus(event.status)
            }

            is ControlEvent.SelectNetwork -> {
                selectNetwork(event.network)
            }

            is ControlEvent.Connect -> {
                if (event.password != null) {
                    connectToWpaWifi(event.context, event.network, event.password)
                } else {
                    connectToOpenWifi(event.context, event.network)
                }
            }

            is ControlEvent.ChangeStopConnecting -> {
                changeStopConnecting(event.state)
            }

            is ControlEvent.ChangeCheckIn -> {
                changeCheckIn(event.state)
            }
        }
    }

    private fun changeScreenStatus(status: ControlStatus) {
        state.value = status
    }

    private fun selectNetwork(network: ScanResult?) {
        selectedNetwork.value = network
    }

    private fun changeStopConnecting(state: Boolean) {
        stopConnecting.value = state
    }

    private fun changeCheckIn(state: Boolean) {
        checkIn.value = state
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
        _wifiResults.addAll(getSortedScanResults(connectedNetwork.value))
    }

    private fun scanFailure() {
        _wifiResults.clear()
        _wifiResults.addAll(getSortedScanResults(connectedNetwork.value))
    }

    private fun getSortedScanResults(targetBSSID: String?): List<ScanResult> {
        val scanResults = wifiManager.scanResults.filter {
            it.SSID.isNotBlank() && (it.capabilities.contains("WPA")
                    || it.capabilities.contains("ESS"))
        }

        val targetNetwork = scanResults.find { it.BSSID == targetBSSID }
        val otherNetworks = scanResults.filter { it.BSSID != targetBSSID }

        return if (targetNetwork != null) {
            listOf(targetNetwork) + otherNetworks
        } else {
            scanResults
        }
    }

    private fun connectToWpaWifi(context: Context, scanResult: ScanResult, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectToWifiApi29(context, scanResult, password)
        } else {
            connectToWifiLegacy(wifiManager, scanResult, password)
        }
    }

    private fun connectToOpenWifi(context: Context, scanResult: ScanResult) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectToWifiApi29(context, scanResult, null)
        } else {
            connectToOpenWifiLegacy(wifiManager, scanResult)
        }
    }

    @Suppress("DEPRECATION")
    private fun connectToOpenWifiLegacy(wifiManager: WifiManager, scanResult: ScanResult) {
        val wifiConfig = WifiConfiguration().apply {
            SSID = scanResult.SSID
            BSSID = scanResult.BSSID
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        }

        val networkId = wifiManager.addNetwork(wifiConfig)
        if (networkId != -1) {
            wifiManager.disconnect()
            wifiManager.enableNetwork(networkId, true)
            viewModelScope.launch {
                var time = Constants.WIFI_TIMEOUT_DEFAULT
                connecting.value = true
                while (!wifiManager.reconnect()) {
                    delay(time)
                    time += Constants.WIFI_TIMEOUT_ADD
                }
                connecting.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToWifiApi29(context: Context, scanResult: ScanResult, password: String?) {
        val wifiNetworkSpecifier = if (password != null) {
            WifiNetworkSpecifier.Builder()
                .setSsid(scanResult.SSID)
                .setBssid(MacAddress.fromString(scanResult.BSSID))
                .setWpa2Passphrase(password)
                .build()
        } else {
            WifiNetworkSpecifier.Builder()
                .setSsid(scanResult.SSID)
                .setBssid(MacAddress.fromString(scanResult.BSSID))
                .build()
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .build()

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val handler = Handler(Looper.getMainLooper())
        var attempt = 0
        val baseDelay = Constants.WIFI_TIMEOUT_DEFAULT

        connecting.value = true

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                connectivityManager.bindProcessToNetwork(network)
                connecting.value = false
                changeCheckIn(true)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                attempt++
                val delay = baseDelay * attempt
                handler.postDelayed({
                    if (stopConnecting.value) {
                        connectivityManager.unregisterNetworkCallback(this)
                        connecting.value = false
                        changeStopConnecting(false)
                    } else {
                        connectivityManager.requestNetwork(networkRequest, this)
                    }
                }, delay)
            }
        }

        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    @Suppress("DEPRECATION")
    private fun connectToWifiLegacy(
        wifiManager: WifiManager,
        scanResult: ScanResult,
        password: String
    ) {
        val wifiConfig = WifiConfiguration().apply {
            SSID = scanResult.SSID
            BSSID = scanResult.BSSID
            preSharedKey = password
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        }

        val networkId = wifiManager.addNetwork(wifiConfig)
        if (networkId != -1) {
            wifiManager.disconnect()
            wifiManager.enableNetwork(networkId, true)
            wifiManager.reconnect()
            //
        }
    }
}