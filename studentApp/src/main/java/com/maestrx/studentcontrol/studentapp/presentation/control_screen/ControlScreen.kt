package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maestrx.studentcontrol.studentapp.R
import com.maestrx.studentcontrol.studentapp.domain.model.PersonalData
import com.maestrx.studentcontrol.studentapp.domain.model.WifiState
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.components.NetworkCard
import com.maestrx.studentcontrol.studentapp.util.WifiService
import com.maestrx.studentcontrol.studentapp.util.WifiStateReceiver

@Composable
internal fun ControlScreen(
    viewModel: ControlViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    viewModel.changeWifiModuleState(WifiService.isWifiEnabled(context))
    val state by viewModel.state.collectAsStateWithLifecycle()
    ControlContent(viewModel, state)
}

@Composable
fun ControlContent(
    viewModel: ControlViewModel,
    state: ControlUiState,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val wifiStateReceiver = WifiStateReceiver(viewModel)

    val filter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
    context.registerReceiver(wifiStateReceiver, filter)

    lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            context.unregisterReceiver(wifiStateReceiver)
        }
    })

    // UI

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                    text = if (state.currentWifi.isBlank()) {
                        stringResource(id = R.string.no_connection)
                    } else {
                        stringResource(id = R.string.current_network, state.currentWifi)
                    },
                    fontSize = 16.sp,
                )
                Button(
                    enabled = state.currentWifi.isNotBlank(),
                    onClick = {
                        // disconnect, go to NetworksScreen
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.disconnect),
                        fontSize = 16.sp,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            when (state.wifiState) {
                is WifiState.Down -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(bottom = 8.dp),
                            text = stringResource(id = R.string.wifi_is_down),
                            fontSize = 18.sp,
                        )
                        Button(
                            onClick = {
                                startActivity(context, Intent(Settings.ACTION_WIFI_SETTINGS), null)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.turn_on),
                                fontSize = 16.sp,
                            )
                        }
                    }
                }

                WifiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(bottom = 8.dp),
                            text = stringResource(id = R.string.loading_wifi),
                            fontSize = 18.sp,
                        )
                        CircularProgressIndicator()
                    }
                }

                WifiState.Idle -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                    ) {
                        itemsIndexed(state.networks) { _, item ->
                            NetworkCard(item, state.currentWifi)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = state.currentWifi.isNotBlank(),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    // check in, LoadingScreen
                },
            ) {
                Text(
                    text = stringResource(id = R.string.check_in),
                    fontSize = 20.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ControlPreview() {
    ControlContent(
        ControlViewModel(),
        ControlUiState(
            wifiState = WifiState.Idle,
            personalData = PersonalData("АВТ-042", "Иванов Иван Иванович"),
            currentWifi = "AndroidShare-1236",
            networks = listOf("AndroidShare-1236", "AndroidShare-8236", "AndroidShare-1256")
        )
    )
}