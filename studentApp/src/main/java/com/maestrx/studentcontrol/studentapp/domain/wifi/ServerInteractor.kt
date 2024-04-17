package com.maestrx.studentcontrol.studentapp.domain.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Secure
import android.util.Log
import com.maestrx.studentcontrol.studentapp.domain.model.Student
import com.maestrx.studentcontrol.studentapp.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    private val wifiHelper: WifiHelper,
) {
    interface StudentListCallback {
        fun onStudentsReceived(students: List<Student>)
    }

    var studentListCallback: StudentListCallback? = null

    private val serverPort = 5951
    private lateinit var socket: DatagramSocket

    var stId = 0L

    @SuppressLint("HardwareIds")
    suspend fun dataExchange() = withContext(Dispatchers.IO) {
        closeSocket()
        socket = DatagramSocket(serverPort)
        Log.d(Constants.DEBUG_TAG, "Socket is open")
        val serverAddress = requireNotNull(wifiHelper.getGatewayIpAddress())

        val deviceId = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
        send(serverAddress, deviceId)

        var timer = launch {
            delay(3_000)
            throw Exception("Timeout")
        }
        var data = receive()
        timer.cancel()

        if (data == "ACK$deviceId") {
            // Send to screen success
            Log.d(Constants.DEBUG_TAG, "Student attended!")
        } else {
            val list = Json.decodeFromString(ListSerializer(Student.serializer()), data)
            studentListCallback?.onStudentsReceived(list)

            launch {
                while (stId == 0L) {
                }
            }.join()

            send(serverAddress, stId.toString())
            stId = 0L

            timer = launch {
                delay(3_000)
                throw Exception("Timeout")
            }
            data = receive()
            timer.cancel()

            if (data == "ACK2$deviceId") {
                // Success
            } else {
                // Error
            }
        }
    }

    private fun send(serverAddress: InetAddress, message: String) {
        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
        socket.send(sendPacket)
        Log.d(Constants.DEBUG_TAG, "Sent data: $message")
    }

    private fun receive(): String {
        val buffer = ByteArray(1024)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        socket.receive(receivePacket)
        val data = String(receivePacket.data, 0, receivePacket.length)
        Log.d(Constants.DEBUG_TAG, "Received data: $data")
        return data
    }

    fun closeSocket() {
        if (::socket.isInitialized && socket.isBound) {
            socket.close()
            Log.d(Constants.DEBUG_TAG, "Socket closed")
        }
    }
}