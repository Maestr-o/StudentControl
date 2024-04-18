package com.maestrx.studentcontrol.studentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maestrx.studentcontrol.studentapp.data.SharedPreferencesManager
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlScreen
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlViewModel
import com.maestrx.studentcontrol.studentapp.presentation.loading_screen.LoadingScreen
import com.maestrx.studentcontrol.studentapp.presentation.loading_screen.LoadingViewModel
import com.maestrx.studentcontrol.studentapp.ui.theme.StudentControlTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentControlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Control.route
                    ) {
                        composable(route = Screen.Control.route) {
                            val viewModel = hiltViewModel<ControlViewModel>()
                            val state = viewModel.state
                            ControlScreen(
                                navController = navController,
                                state = state.value,
                                onEvent = viewModel::onEvent,
                                prefs = prefs
                            )
                        }

                        composable(route = Screen.Loading.route) {
                            val viewModel = hiltViewModel<LoadingViewModel>()
                            val state = viewModel.state
                            LoadingScreen(
                                navController = navController,
                                state = state,
                                onEvent = viewModel::onEvent,
                                prefs = prefs,
                            )
                        }
                    }
                }
            }
        }
    }
}