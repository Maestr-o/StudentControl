package com.maestrx.studentcontrol.studentapp.presentation.control_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maestrx.studentcontrol.studentapp.R
import com.maestrx.studentcontrol.studentapp.ui.theme.ConnectedNetwork

@Composable
fun NetworkCard(network: String, currentNetwork: String) {
    Card(
        modifier = Modifier
            .padding(vertical = 3.dp),
        colors = if (currentNetwork != network) {
            CardDefaults.cardColors(containerColor = Color.Unspecified)
        } else {
            CardDefaults.cardColors(containerColor = ConnectedNetwork)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.network_wifi),
                contentDescription = "network"
            )
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = network,
                fontSize = 16.sp,
            )
        }
    }
}