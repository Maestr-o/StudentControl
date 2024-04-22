package com.maestrx.studentcontrol.studentapp.presentation.control_screen

import android.content.Intent
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.maestrx.studentcontrol.studentapp.ui.theme.Connected

@Composable
internal fun ControlScreen(
    state: ControlStatus,
    personalData: PersonalData?,
    isDataExchanged: Boolean,
    navClick: () -> Unit,
) {
    val context = LocalContext.current

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
                            .padding(start = 14.dp)
                            .weight(1f),
                        textAlign = TextAlign.Start,
                        text = when (state) {
                            is ControlStatus.NotConnected -> {
                                stringResource(id = R.string.no_connection)
                            }

                            is ControlStatus.Connected -> {
                                stringResource(id = R.string.connected)
                            }
                        },
                        fontSize = 17.sp,
                    )

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
                is ControlStatus.NotConnected -> {
                    DisconnectedGroup()
                }

                is ControlStatus.Connected -> {
                    ConnectedGroup(navClick, isDataExchanged)
                }
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
fun ConnectedGroup(
    navClick: () -> Unit,
    isDataExchanged: Boolean,
) {
    val buttonSize = 165.dp
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            modifier = Modifier
                .size(buttonSize),
            enabled = !isDataExchanged,
            onClick = {
                navClick()
            },
        ) {
            Image(
                modifier = Modifier
                    .size(buttonSize),
                painter = painterResource(id = R.drawable.control_start),
                contentDescription = "start control",
            )
        }
        Text(
            modifier = Modifier
                .padding(top = 8.dp),
            text = stringResource(
                id = if (isDataExchanged) {
                    R.string.checked_in
                } else {
                    R.string.check_in
                }
            ),
            color = Connected,
            fontSize = 22.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ControlPreview() {
    ControlScreen(
        navClick = {},
        state = ControlStatus.Connected,
        personalData = PersonalData(
            group = "АВТ-042",
            fullName = "Сидоров Иван Сидорович"
        ),
        isDataExchanged = true,
    )
}