package com.maestrx.studentcontrol.studentapp

import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maestrx.studentcontrol.studentapp.broadcast_receiver.LocationReceiver
import com.maestrx.studentcontrol.studentapp.broadcast_receiver.WifiReceiver
import com.maestrx.studentcontrol.studentapp.data.SharedPreferencesManager
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlScreen
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlStatus
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlViewModel
import com.maestrx.studentcontrol.studentapp.presentation.loading_screen.LoadingScreen
import com.maestrx.studentcontrol.studentapp.presentation.loading_screen.LoadingViewModel
import com.maestrx.studentcontrol.studentapp.presentation.settings_screen.PermissionsScreen
import com.maestrx.studentcontrol.studentapp.ui.theme.StudentAppTheme
import com.maestrx.studentcontrol.studentapp.util.Constants
import com.maestrx.studentcontrol.studentapp.util.WifiHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: SharedPreferencesManager

    private lateinit var locationReceiver: LocationReceiver
    private lateinit var wifiReceiver: WifiReceiver
    private var isDataExchanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    var isLocationEnabled by rememberSaveable {
                        mutableStateOf(LocationReceiver.isLocationEnabled(applicationContext))
                    }
                    locationReceiver = LocationReceiver { enabled ->
                        isLocationEnabled = enabled
                    }
                    val locationIntentFilter =
                        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
                    registerReceiver(locationReceiver, locationIntentFilter)

                    var isWifiEnabled by rememberSaveable {
                        mutableStateOf(WifiReceiver.isWifiEnabled(applicationContext))
                    }
                    wifiReceiver = WifiReceiver { enabled ->
                        isWifiEnabled = enabled
                    }
                    val wifiIntentFilter =
                        IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
                    registerReceiver(wifiReceiver, wifiIntentFilter)

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Settings.route,
                    ) {
                        composable(
                            route = Screen.Settings.route,
                        ) {
                            PermissionsScreen(isLocationEnabled) {
                                navController.navigate(route = Screen.Control.route + "/false")
                            }
                        }

                        composable(
                            route = Screen.Control.route + "/{${Constants.IS_DATA_EXCHANGED}}",
                            arguments = listOf(
                                navArgument(Constants.IS_DATA_EXCHANGED) {
                                    type = NavType.BoolType
                                }
                            ),
                        ) { entry ->
                            val personalData = prefs.getPersonalData()

                            val viewModel = hiltViewModel<ControlViewModel>()
                            viewModel.startWifiScan(applicationContext)

                            isDataExchanged =
                                entry.arguments?.getBoolean(Constants.IS_DATA_EXCHANGED) ?: false

                            var isWifiConnected by rememberSaveable {
                                mutableStateOf(false)
                            }
                            val connectedNetwork = rememberSaveable {
                                mutableStateOf<String?>(null)
                            }
                            WifiStateReceiverCompose { isConnected, bssid ->
                                isWifiConnected = isConnected
                                connectedNetwork.value = bssid
                                viewModel.connectedNetwork = connectedNetwork
                            }

                            ControlScreen(
                                state = if (isDataExchanged) {
                                    ControlStatus.Completed
                                } else if (isWifiConnected) {
                                    ControlStatus.Connected
                                } else if (isWifiEnabled) {
                                    ControlStatus.WifiIsUp
                                } else {
                                    ControlStatus.WifiIsDown
                                },
                                appContext = applicationContext,
                                personalData = personalData,
                                isLocationEnabled = isLocationEnabled,
                                wifiResults = viewModel.wifiResults,
                                connectedNetwork = connectedNetwork.value,
                                onEvent = viewModel::onEvent,
                                selectedNetwork = viewModel.selectedNetwork.value,
                                connecting = viewModel.connecting.value,
                                checkIn = viewModel.checkIn.value,
                                badState = {
                                    navController.navigateUp()
                                },
                            ) {
                                navController.navigate(Screen.Loading.route)
                            }
                        }

                        composable(
                            route = Screen.Loading.route,
                        ) {
                            val viewModel = hiltViewModel<LoadingViewModel>()
                            val state = viewModel.state
                            LoadingScreen(
                                state = state,
                                onEvent = viewModel::onEvent,
                                prefs = prefs,
                                isLocationEnabled = isLocationEnabled,
                                isWifiEnabled = isWifiEnabled,
                                badState = {
                                    navController.navigateUp()
                                },
                            ) { isConnected ->
                                navController.popBackStack()
                                navController.popBackStack()
                                navController.navigate(Screen.Control.route + "/$isConnected")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
        unregisterReceiver(wifiReceiver)
    }

    @Composable
    fun WifiStateReceiverCompose(isConnected: (Boolean, String?) -> Unit) {
        val context = LocalContext.current

        DisposableEffect(key1 = context) {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    isConnected(true, WifiHelper.getCurrentBSSID(applicationContext))
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    isConnected(false, null)
                }
            }

            val connectivityManager =
                context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

            onDispose {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }
    }
}