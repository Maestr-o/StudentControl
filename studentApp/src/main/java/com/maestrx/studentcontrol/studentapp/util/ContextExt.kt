package com.maestrx.studentcontrol.studentapp.util

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun Context.Toast(res: Int) {
    Toast.makeText(this, stringResource(id = res), Toast.LENGTH_LONG).show()
}