package com.maestrx.studentcontrol.studentapp.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager

class LocationReceiver(private val onLocationEnabled: (Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            onLocationEnabled(isLocationEnabled(context))
        }
    }

    companion object {
        fun isLocationEnabled(context: Context): Boolean =
            (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
    }
}