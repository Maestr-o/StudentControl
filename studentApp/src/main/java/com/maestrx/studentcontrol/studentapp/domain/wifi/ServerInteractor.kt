package com.maestrx.studentcontrol.studentapp.domain.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.provider.Settings.Secure
import android.util.Log
import com.maestrx.studentcontrol.studentapp.domain.model.Student
import com.maestrx.studentcontrol.studentapp.presentation.loading_screen.LoadingStatus
import com.maestrx.studentcontrol.studentapp.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
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
import java.net.SocketTimeoutException
import javax.inject.Inject

@ViewModelScoped
class ServerInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
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

        val wm: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val serverAddress = InetAddress.getByAddress(intToByteArray(wm.dhcpInfo.gateway))

        val deviceId = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
        send(serverAddress, defaultServerPort, deviceId)

        var receivePacket = receive()
        var data = getData(receivePacket)
        if (data == "ACK$deviceId") {
            _interState.update { LoadingStatus.Success }
        } else {
            val list = Json.decodeFromString(ListSerializer(Student.serializer()), data)
            studentListCallback?.onStudentsReceived(list)

            launch {
                while (stId == 0L) {
                }
            }.join()

            send(serverAddress, receivePacket.port, stId.toString())
            stId = 0L

            receivePacket = receive()
            data = getData(receivePacket)
            if (data == "ACK2$deviceId") {
                _interState.update { LoadingStatus.Success }
            } else {
                _interState.update { LoadingStatus.Error }
            }
        }
    }

    private fun send(serverAddress: InetAddress, serverPort: Int, message: String) {
        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
        socket.send(sendPacket)
        Log.d(Constants.DEBUG_TAG, "Sent data to port $serverPort: $message")
    }

    private fun receive(): DatagramPacket {
        val buffer = ByteArray(50000)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        socket.soTimeout = Constants.TIMEOUT
        try {
            socket.receive(receivePacket)
            return receivePacket
        } catch (e: SocketTimeoutException) {
            throw Exception("Receive operation timed out", e)
        }
    }

    private fun getData(packet: DatagramPacket): String {
        val data = String(packet.data, 0, packet.length)
        Log.d(Constants.DEBUG_TAG, "Received data: $data")
        return data
    }

    private fun intToByteArray(value: Int): ByteArray {
        val byteBuffer = ByteArray(4)
        byteBuffer[0] = (value and 0xFF).toByte()
        byteBuffer[1] = (value shr 8 and 0xFF).toByte()
        byteBuffer[2] = (value shr 16 and 0xFF).toByte()
        byteBuffer[3] = (value shr 24 and 0xFF).toByte()
        return byteBuffer
    }

    fun closeSocket() {
        if (::socket.isInitialized && socket.isBound) {
            socket.close()
            Log.d(Constants.DEBUG_TAG, "Socket closed")
        }
    }
}