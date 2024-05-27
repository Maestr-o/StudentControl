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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: SharedPreferencesManager

    private lateinit var locationReceiver: LocationReceiver
    private lateinit var wifiReceiver: WifiReceiver
    private var wifiState: Int = 0 // 0 - off, 1 - on, 2 - connected

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

                            var wifiState by remember {
                                mutableIntStateOf(0) // 0 - off, 1 - on, 2 - connected
                            }

                            val isDataExchanged =
                                entry.arguments?.getBoolean(Constants.IS_DATA_EXCHANGED) ?: false

                            var state by remember {
                                mutableStateOf(
                                    when {
                                        isDataExchanged -> ControlStatus.Completed
                                        wifiState == 2 -> ControlStatus.Connected
                                        wifiState == 1 -> ControlStatus.WifiIsUp
                                        else -> ControlStatus.WifiIsDown
                                    }
                                )
                            }

                            WifiStateReceiverCompose { isConnected ->
                                wifiState = when {
                                    isConnected -> 2
                                    isWifiEnabled -> 1
                                    else -> 0
                                }

                                state = when {
                                    isDataExchanged -> ControlStatus.Completed
                                    wifiState == 2 -> ControlStatus.Connected
                                    wifiState == 1 -> ControlStatus.WifiIsUp
                                    else -> ControlStatus.WifiIsDown
                                }
                            }

                            ControlScreen(
                                state = state,
                                personalData = personalData,
                                isLocationEnabled = isLocationEnabled,
                                isDataExchanged = isDataExchanged,
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
                                isWifiEnabled = wifiState >= 1,
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
    fun WifiStateReceiverCompose(isConnected: (Boolean) -> Unit) {
        val context = LocalContext.current

        DisposableEffect(key1 = context) {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    isConnected(true)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    isConnected(false)
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