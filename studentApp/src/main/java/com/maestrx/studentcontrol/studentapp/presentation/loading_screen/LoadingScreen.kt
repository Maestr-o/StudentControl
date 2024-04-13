package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.maestrx.studentcontrol.studentapp.R

@Composable
fun LoadingScreen(
    navController: NavController,
    viewModel: LoadingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state.screenState) {
        is LoadingState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    text = stringResource(id = R.string.sending_data),
                    fontSize = 20.sp,
                )
                CircularProgressIndicator()
            }
            SideEffect {
                viewModel.dataExchange()
            }
        }

        is LoadingState.Input -> {
//            val groups = mutableSetOf<String>()
//            state.list.forEach {
//                groups += it.group
//            }
//            val groups = listOf("123", "456", "789")
//
//            var expanded by rememberSaveable { mutableStateOf(false) }
//            var selectedGroup by rememberSaveable { mutableStateOf<String?>(null) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoadingPreview() {
    val context = LocalContext.current
    LoadingScreen(
        navController = NavController(context),
    )
}