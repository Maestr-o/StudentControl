package com.maestrx.studentcontrol.studentapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings.Secure
import android.util.Log
import com.maestrx.studentcontrol.studentapp.domain.model.Student
import com.maestrx.studentcontrol.studentapp.presentation.loading_screen.LoadingStatus
import com.maestrx.studentcontrol.studentapp.util.Constants
import com.maestrx.studentcontrol.studentapp.util.NetworkCallbackManager
import com.maestrx.studentcontrol.studentapp.util.WifiHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

@ViewModelScoped
class ServerInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkCallbackManager: NetworkCallbackManager,
) {
    interface StudentListCallback {
        fun onStudentsReceived(students: List<Student>)
    }

    private val _interState = MutableStateFlow<LoadingStatus>(LoadingStatus.Loading)
    val interState = _interState.asStateFlow()

    var studentListCallback: StudentListCallback? = null

    private val defaultServerPort = 5951
    private val defaultClientPort = 5952
    private lateinit var socket: DatagramSocket

    var stId = 0L

    @SuppressLint("HardwareIds")
    suspend fun dataExchange() = withContext(Dispatchers.IO) {
        closeSocket()
        socket = DatagramSocket(defaultClientPort)
        Log.d(Constants.DEBUG_TAG, "Socket is open in port: ${socket.localPort}")

        val serverAddress = WifiHelper.getServerAddress(context)

        val deviceId = Secure.getString(context.contentResolver, Secure.ANDROID_ID)

        var receivePacket = sendReceiveCycle(serverAddress, defaultServerPort, deviceId)
        val newPort = receivePacket.port

        var data = extractData(receivePacket)
        if (data == "ACK$deviceId") {
            _interState.update { LoadingStatus.Success }
        } else {
            val list = Json.decodeFromString(ListSerializer(Student.serializer()), data)
            if (list.isEmpty()) {
                _interState.update { LoadingStatus.Error }
                return@withContext
            }
            studentListCallback?.onStudentsReceived(list)

            launch {
                while (stId == 0L) {
                    delay(100)
                }
            }.join()

            receivePacket = sendReceiveCycle(serverAddress, newPort, stId.toString())

            stId = 0L

            data = extractData(receivePacket)
            if (data == "ACK2$deviceId") {
                _interState.update { LoadingStatus.Success }
            } else {
                _interState.update { LoadingStatus.Error }
            }
        }
    }

    private fun sendReceiveCycle(address: InetAddress, port: Int, data: String): DatagramPacket {
        val buffer = ByteArray(Constants.PACKET_BUFFER_SIZE)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        var attempts = 0
        var success = false

        while (attempts < Constants.ATTEMPTS && !success) {
            try {
                send(address, port, data)
                socket.soTimeout = Constants.TIMEOUT
                socket.receive(receivePacket)
                success = true
            } catch (e: Exception) {
                attempts++
            }
        }

        if (!success) {
            Log.d(Constants.DEBUG_TAG, "Failed to exchange data")
            throw Exception()
        }

        return receivePacket
    }

    private fun send(serverAddress: InetAddress, serverPort: Int, message: String) {
        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
        socket.send(sendPacket)
        Log.d(Constants.DEBUG_TAG, "Sent data to port $serverPort: $message")
    }

    private fun extractData(packet: DatagramPacket): String {
        val data = String(packet.data, 0, packet.length)
        Log.d(Constants.DEBUG_TAG, "Received data: $data")
        return data
    }

    fun closeSocket() {
        if (::socket.isInitialized && socket.isBound) {
            socket.close()
            Log.d(Constants.DEBUG_TAG, "Socket closed")
            disconnect()
        }
    }

    private fun disconnect() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val wifiManager =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiManager.disconnect()
            } else {
                networkCallbackManager.unregisterNetworkCallback()
            }
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_TAG, "Can't disconnect from Wi-Fi network: $e")
        }
    }
}