package com.maestrx.studentcontrol.studentapp.domain.wifi

import android.content.Context
import android.net.DhcpInfo
import android.net.wifi.WifiManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val wm: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val dhcpInfo: DhcpInfo = wm.dhcpInfo
    private val gateway = dhcpInfo.gateway

    fun getGatewayIpAddress(): InetAddress? =
        InetAddress.getByAddress(intToByteArray(gateway))

    fun disconnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        } else {
            wm.disconnect()
        }
    }

    private fun intToByteArray(value: Int): ByteArray {
        val byteBuffer = ByteArray(4)
        byteBuffer[0] = (value and 0xFF).toByte()
        byteBuffer[1] = (value shr 8 and 0xFF).toByte()
        byteBuffer[2] = (value shr 16 and 0xFF).toByte()
        byteBuffer[3] = (value shr 24 and 0xFF).toByte()
        return byteBuffer
    }
}