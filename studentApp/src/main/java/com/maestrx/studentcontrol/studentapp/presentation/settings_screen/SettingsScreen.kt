package com.maestrx.studentcontrol.studentapp.presentation.settings_screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.maestrx.studentcontrol.studentapp.R

@Composable
fun PermissionsScreen(
    isLocationEnabled: Boolean,
    navClick: () -> Unit,
) {
    val context = LocalContext.current

    BackHandler {
        (context as? Activity)?.finish()
    }

    var isFineLocationGranted by rememberSaveable {
        mutableStateOf(
            checkFineLocationPermission(
                context
            )
        )
    }

    val permissionLauncher: ActivityResultLauncher<String> =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isFineLocationGranted = isGranted
            if (checkPermissions(context) && isLocationEnabled) {
                navClick()
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            modifier = Modifier
                .padding(top = 8.dp),
            text = stringResource(id = R.string.check_settings),
            fontSize = 22.sp
        )
        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = stringResource(id = R.string.settings_info),
            fontSize = 18.sp
        )
        Button(
            modifier = Modifier
                .padding(top = 24.dp)
                .width(220.dp),
            onClick = {
                requestPermission(context, permissionLauncher)
            },
            enabled = !checkLocalPermissionsFlags(isFineLocationGranted),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = stringResource(id = R.string.request_permission),
                textAlign = TextAlign.Center
            )
        }
        Button(
            modifier = Modifier
                .padding(top = 6.dp)
                .width(220.dp),
            onClick = {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            },
            enabled = !isLocationEnabled,
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = stringResource(id = R.string.enable_location),
                textAlign = TextAlign.Center
            )
        }
    }

    if (checkPermissions(context) && isLocationEnabled) {
        navClick()
    }
}

private fun checkFineLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

private fun checkPermissions(
    context: Context,
): Boolean {
    val sdkVersion = Build.VERSION.SDK_INT
    return if (sdkVersion >= Build.VERSION_CODES.P && (checkFineLocationPermission(context))) {
        true
    } else if (sdkVersion < Build.VERSION_CODES.P) {
        true
    } else {
        false
    }
}

private fun checkLocalPermissionsFlags(fineLocation: Boolean): Boolean {
    val sdkVersion = Build.VERSION.SDK_INT
    return if (sdkVersion >= Build.VERSION_CODES.P && fineLocation) {
        true
    } else if (sdkVersion < Build.VERSION_CODES.P) {
        true
    } else {
        false
    }
}

private fun requestPermission(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>
) {
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    if (ContextCompat.checkSelfPermission(context, permission)
        != PackageManager.PERMISSION_GRANTED
    ) {
        permissionLauncher.launch(permission)
    }
}