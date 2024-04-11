package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrx.studentcontrol.studentapp.R
import com.maestrx.studentcontrol.studentapp.domain.model.PersonalData
import com.maestrx.studentcontrol.studentapp.domain.model.WifiState
import com.maestrx.studentcontrol.studentapp.util.WifiManagerHelper

@Composable
internal fun ControlScreen(
    viewModel: ControlViewModel = hiltViewModel(),
    wifiManager: WifiManagerHelper,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ControlContent(viewModel, state, wifiManager)
}

@Composable
fun ControlContent(
    viewModel: ControlViewModel,
    state: ControlUiState,
    wifiManager: WifiManagerHelper,
) {
    val context = LocalContext.current
    WifiStateReceiverCompose(viewModel, wifiManager)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    text = when (state.wifiState) {
                        is WifiState.NotConnected -> {
                            stringResource(id = R.string.no_connection)
                        }

                        is WifiState.Connected -> {
                            stringResource(id = R.string.current_network, state.wifiState.network)
                        }
                    },
                    fontSize = 16.sp,
                )

                if (state.wifiState is WifiState.Connected) {
                    IconButton(
                        onClick = {
                            wifiManager.disconnect()
                        },
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_signal_wifi_off_24
                            ),
                            contentDescription = stringResource(
                                id = R.string.disconnect
                            ),
                        )
                    }
                }

                IconButton(
                    onClick = {
                        startActivity(context, Intent(Settings.ACTION_WIFI_SETTINGS), null)
                    },
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.baseline_settings_24
                        ),
                        contentDescription = stringResource(
                            id = R.string.go_to_settings
                        ),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (state.wifiState) {
                is WifiState.NotConnected -> {
                    DisconnectedGroup()
                }

                is WifiState.Connected -> {
                    ConnectedGroup()
                }
            }
        }

        if (state.personalData.fullName.isNotBlank() && state.personalData.group.isNotBlank()) {
            Card(
                modifier = Modifier
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = stringResource(
                        id = R.string.data_placeholder,
                        state.personalData.group,
                        state.personalData.fullName
                    ),
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
fun DisconnectedGroup() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .padding(bottom = 8.dp),
            text = stringResource(id = R.string.connect_to_network),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = {
                startActivity(context, Intent(Settings.ACTION_WIFI_SETTINGS), null)
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.go_to_settings),
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun ConnectedGroup() {
    val buttonSize = 200.dp
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            modifier = Modifier
                .size(buttonSize),
            onClick = {
                // check in, LoadingScreen
            },
        ) {
            Image(
                modifier = Modifier
                    .size(buttonSize),
                painter = painterResource(id = R.drawable.start_control),
                contentDescription = "start control"
            )
        }
        Text(
            modifier = Modifier
                .padding(top = 8.dp),
            text = stringResource(id = R.string.check_in),
            fontSize = 20.sp,
        )
    }
}

@Composable
fun WifiStateReceiverCompose(viewModel: ControlViewModel, wifiManager: WifiManagerHelper) {
    val context = LocalContext.current

    DisposableEffect(key1 = context) {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                viewModel.changeWifiState(wifiManager.getSSID())
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                viewModel.changeWifiState(null)
            }
        }

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ControlPreview() {
    ControlContent(
        ControlViewModel(),
        ControlUiState(
            wifiState = WifiState.Connected("AndroidShare_5478"),
            personalData = PersonalData("АВТ-042", "Иванов Иван Иванович"),
        ),
        WifiManagerHelper(LocalContext.current),
    )
}