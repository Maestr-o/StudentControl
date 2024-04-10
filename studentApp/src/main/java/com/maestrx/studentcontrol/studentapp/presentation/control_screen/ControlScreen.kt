package com.maestrx.studentcontrol.studentapp.presentation.control_screen

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maestrx.studentcontrol.studentapp.R
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.components.NetworkCard

@Composable
internal fun ControlScreen() {
    ControlContent()
}

@Preview(showBackground = true)
@Composable
fun ControlContent() {
    val networks = listOf(
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_1234",
        "AndroidShare_8744",
        "Ntk-58_5G"
    )

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
                    text = stringResource(id = R.string.current_network, "AndroidShare_1236"),
                    fontSize = 16.sp,
                )
                Button(
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                itemsIndexed(networks) { _, item ->
                    NetworkCard(item)
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        "АВТ-042",
                        "Иванов Иван Иванович"
                    ),
                    fontSize = 16.sp,
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth(),
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