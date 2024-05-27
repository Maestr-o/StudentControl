package com.maestrx.studentcontrol.studentapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.maestrx.studentcontrol.studentapp.data.SharedPreferencesManager
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlEvent
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlScreen
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlStatus
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlViewModel
import com.maestrx.studentcontrol.studentapp.presentation.loading_screen.LoadingScreen
import com.maestrx.studentcontrol.studentapp.presentation.loading_screen.LoadingViewModel
import com.maestrx.studentcontrol.studentapp.presentation.permissions_screen.PermissionsScreen
import com.maestrx.studentcontrol.studentapp.ui.theme.StudentAppTheme
import com.maestrx.studentcontrol.studentapp.util.Constants
import com.maestrx.studentcontrol.studentapp.util.WifiHelper.isWifiConnected
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: SharedPreferencesManager

    private lateinit var locationReceiver: LocationReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    var isLocationEnabled by remember { mutableStateOf(false) }
                    locationReceiver = LocationReceiver { enabled ->
                        isLocationEnabled = enabled
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Permissions.route,
                    ) {
                        composable(
                            route = Screen.Permissions.route,
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
                            val state = viewModel.state
                            if (state.value is ControlStatus.Default) {
                                state.value = if (isWifiConnected(applicationContext)) {
                                    ControlStatus.Connected
                                } else {
                                    ControlStatus.NotConnected
                                }
                            }
                            WifiStateReceiverCompose(viewModel::onEvent)
                            ControlScreen(
                                state = state.value,
                                personalData = personalData,
                                isDataExchanged = entry.arguments?.getBoolean(Constants.IS_DATA_EXCHANGED)
                                    ?: false,
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
    }

    @Composable
    fun WifiStateReceiverCompose(onEvent: (ControlEvent) -> Unit) {
        val context = LocalContext.current

        DisposableEffect(key1 = context) {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    onEvent(ControlEvent.SetScreenStatus(ControlStatus.Connected))
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    onEvent(ControlEvent.SetScreenStatus(ControlStatus.NotConnected))
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

    class LocationReceiver(private val onLocationEnabled: (Boolean) -> Unit) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null && intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                onLocationEnabled(isGpsEnabled)
            }
        }
    }
}