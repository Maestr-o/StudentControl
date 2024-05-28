package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.maestrx.studentcontrol.studentapp.R
import com.maestrx.studentcontrol.studentapp.domain.model.PersonalData
import com.maestrx.studentcontrol.studentapp.util.Constants
import com.maestrx.studentcontrol.studentapp.util.WifiHelper

@Composable
internal fun ControlScreen(
    appContext: Context,
    state: ControlStatus,
    personalData: PersonalData?,
    isLocationEnabled: Boolean,
    wifiResults: List<ScanResult>?,
    selectedNetwork: ScanResult?,
    connectedNetwork: String?,
    onEvent: (ControlEvent) -> Unit,
    badState: () -> Unit,
    navClick: () -> Unit,
) {
    val context = LocalContext.current

    BackHandler {
        (context as? Activity)?.finish()
    }

    if (!isLocationEnabled) {
        badState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            )
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 14.dp, top = 8.dp, bottom = 8.dp)
                            .weight(1f),
                        textAlign = TextAlign.Start,
                        text = when (state) {
                            is ControlStatus.WifiIsDown -> {
                                stringResource(id = R.string.wifi_down)
                            }

                            is ControlStatus.WifiIsUp -> {
                                stringResource(id = R.string.wifi_up)
                            }

                            is ControlStatus.Connected -> {
                                stringResource(
                                    id = R.string.connected,
                                    WifiHelper.getCurrentSSID(context)
                                )
                            }

                            is ControlStatus.Completed -> {
                                stringResource(id = R.string.checked_in)
                            }
                        },
                        fontSize = 17.sp,
                    )

                    IconButton(
                        onClick = {
                            try {
                                startActivity(context, Intent(Settings.ACTION_WIFI_SETTINGS), null)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    R.string.error_go_to_settings,
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.d(
                                    Constants.DEBUG_TAG,
                                    "Error opening settings: ${e.printStackTrace()}"
                                )
                            }
                        },
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_settings_24
                            ),
                            contentDescription = stringResource(
                                id = R.string.go_to_settings
                            ),
                            tint = Color.White,
                        )
                    }
                }
                if (personalData != null &&
                    personalData.group.isNotBlank() && personalData.fullName.isNotBlank()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        color = Color.White,
                        thickness = 2.dp
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        text = stringResource(
                            id = R.string.data_placeholder,
                            personalData.group,
                            personalData.fullName
                        ),
                        fontSize = 16.sp,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is ControlStatus.WifiIsDown -> WifiIsDownGroup()
                is ControlStatus.WifiIsUp -> WifiIsUpGroup(
                    appContext,
                    wifiResults,
                    selectedNetwork,
                    connectedNetwork,
                    onEvent
                )

                is ControlStatus.Connected -> WifiIsUpGroup(
                    appContext,
                    wifiResults,
                    selectedNetwork,
                    connectedNetwork,
                    onEvent
                )

                is ControlStatus.Completed -> CompletedGroup()
            }
        }
    }
}

@Composable
fun WifiIsDownGroup() {
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
            text = stringResource(id = R.string.turn_on_wifi),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = {
                try {
                    startActivity(context, Intent(Settings.ACTION_WIFI_SETTINGS), null)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        R.string.error_go_to_settings,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(Constants.DEBUG_TAG, "Error opening settings: ${e.printStackTrace()}")
                }
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
fun WifiIsUpGroup(
    appContext: Context,
    wifiResults: List<ScanResult>?,
    selectedNetwork: ScanResult?,
    connectedNetwork: String?,
    onEvent: (ControlEvent) -> Unit,
) {
    var password by remember { mutableStateOf<String?>(null) }

    if (!wifiResults.isNullOrEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            LazyColumn {
                items(wifiResults) { network ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(ControlEvent.SelectNetwork(network))
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = network.SSID,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                        if (connectedNetwork == network.BSSID) {
                            Icon(
                                modifier = Modifier.padding(end = 12.dp),
                                imageVector = Icons.Filled.Check,
                                tint = if (isSystemInDarkTheme()) {
                                    Color.White
                                } else {
                                    Color.Black
                                },
                                contentDescription = "connected"
                            )
                        }
                    }
                }
            }

            selectedNetwork?.let { network ->
                val isRequirePassword = network.capabilities.contains("WPA")

                AlertDialog(
                    onDismissRequest = { onEvent(ControlEvent.SelectNetwork(null)) },
                    title = {
                        Text(
                            text = stringResource(
                                id = R.string.connect_and_check_in,
                                network.SSID
                            )
                        )
                    },
                    text = {
                        if (isRequirePassword) {
                            Column {
                                TextField(
                                    value = password ?: "",
                                    onValueChange = { password = it },
                                    label = { Text(text = stringResource(id = R.string.password)) }
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onEvent(ControlEvent.Connect(network, appContext, password))
                                onEvent(ControlEvent.SelectNetwork(null))
                                password = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            enabled = try {
                                !network.capabilities.contains("WPA")
                                        || (!password.isNullOrBlank() && password?.length!! >= 8)
                            } catch (_: Exception) {
                                false
                            }
                        ) {
                            Text(stringResource(id = R.string.ok))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                onEvent(ControlEvent.SelectNetwork(null))
                                password = null
                            },
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text(stringResource(id = R.string.cancel))
                        }
                    }
                )
            }
        }
    } else {
        CircularProgressIndicator()
    }
}

@Composable
fun CompletedGroup() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
            text = stringResource(id = R.string.checked_in_hint),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = {
                (context as? Activity)?.finish()
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.exit),
                fontSize = 16.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControlPreview() {
    ControlScreen(
        navClick = {},
        state = ControlStatus.WifiIsUp,
        personalData = PersonalData(
            group = "АВТ-042",
            fullName = "Сидоров Иван Сидорович"
        ),
        isLocationEnabled = true,
        wifiResults = null,
        connectedNetwork = null,
        selectedNetwork = null,
        onEvent = {},
        appContext = LocalContext.current,
        badState = {},
    )
}