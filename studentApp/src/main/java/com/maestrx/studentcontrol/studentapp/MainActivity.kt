package com.maestrx.studentcontrol.studentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maestrx.studentcontrol.studentapp.data.SharedPreferencesManager
import com.maestrx.studentcontrol.studentapp.presentation.control_screen.ControlScreen
import com.maestrx.studentcontrol.studentapp.presentation.loading_screen.LoadingScreen
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
                            ControlScreen(navController = navController, prefs = prefs)
                        }

                        composable(route = Screen.Loading.route) {
                            LoadingScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}