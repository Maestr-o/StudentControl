package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.studentapp.util.Constants
import com.maestrx.studentcontrol.studentapp.util.NetworkCallbackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val networkCallbackManager: NetworkCallbackManager,
) : ViewModel() {

    private var state = mutableStateOf<ControlStatus>(ControlStatus.WifiIsDown)

    private val _wifiResults = mutableStateListOf<ScanResult>()
    val wifiResults: List<ScanResult> = _wifiResults
    private lateinit var wifiManager: WifiManager
    private lateinit var handler: Handler
    private val scanInterval: Long = 15000

    var selectedNetwork = mutableStateOf<ScanResult?>(null)
    var connectedNetwork = mutableStateOf<String?>(null)
    var connecting = mutableStateOf(false)
    private var stopConnecting = mutableStateOf(false)
    var checkIn = mutableStateOf(false)

    private var jobConnection: Job? = null

    fun onEvent(event: ControlEvent) {
        when (event) {
            is ControlEvent.SetScreenStatus -> {
                changeScreenStatus(event.status)
            }

            is ControlEvent.SelectNetwork -> {
                selectNetwork(event.network)
            }

            is ControlEvent.Connect -> {
                jobConnection = viewModelScope.launch {
                    try {
                        if (event.password != null) {
                            connectToWpaWifi(event.context, event.network, event.password)
                        } else {
                            connectToOpenWifi(event.context, event.network)
                        }
                    } catch (e: Exception) {
                        Log.d(Constants.DEBUG_TAG, "Can't connect to Wi-Fi network: $e")
                    }
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
        if (state) {
            jobConnection?.cancel()
            connecting.value = false
            selectNetwork(null)
            changeStopConnecting(false)
        }
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
        if (!checkFineLocationPermission()) {
            return emptyList()
        }
        val scanResults = wifiManager.scanResults.filter {
            it.SSID.isNotBlank() && (it.capabilities.contains("WPA")
                    || it.capabilities.contains("ESS"))
        }
            .sortedBy {
                it.SSID.lowercase()
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
            connectToWifiLegacy(wifiManager, scanResult, null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToWifiApi29(context: Context, scanResult: ScanResult, password: String?) {
        networkCallbackManager.registerNetworkCallback(
            ssid = scanResult.SSID,
            bssid = scanResult.BSSID,
            password = password,
            onNetworkAvailable = {
                connecting.value = false
                changeCheckIn(true)
            },
            onNetworkUnavailable = {
                if (stopConnecting.value) {
                    networkCallbackManager.unregisterNetworkCallback()
                    changeStopConnecting(false)
                }
            }
        )

        connecting.value = true
    }

    @Suppress("DEPRECATION")
    private fun connectToWifiLegacy(
        wifiManager: WifiManager,
        scanResult: ScanResult,
        password: String?
    ) {
        val wifiConfig = WifiConfiguration().apply {
            SSID = scanResult.SSID
            BSSID = scanResult.BSSID
            if (password.isNullOrBlank()) {
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            } else {
                preSharedKey = "\"" + password + "\""
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            }
        }

        val networkId = wifiManager.addNetwork(wifiConfig)
        if (networkId != -1) {
            viewModelScope.launch(Dispatchers.IO) {
                var connected: Boolean
                var attempt = 1

                connecting.value = true
                while (true) {
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(networkId, true)
                    wifiManager.reconnect()

                    delay(Constants.WIFI_TIMEOUT_DEFAULT)

                    connected = checkConnection(wifiManager, scanResult.SSID)
                    if (connected) {
                        break
                    }

                    delay(attempt * Constants.WIFI_TIMEOUT_DEFAULT)

                    if (stopConnecting.value) {
                        changeStopConnecting(false)
                        break
                    }

                    attempt++
                }
                connecting.value = false
                if (connected) {
                    changeCheckIn(true)
                }
            }
        }
    }

    private fun checkConnection(wifiManager: WifiManager, ssid: String): Boolean {
        val connectionInfo = wifiManager.connectionInfo
        return connectionInfo != null && connectionInfo.ssid == "\"$ssid\"" && connectionInfo.networkId != -1
    }

    private fun checkFineLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}