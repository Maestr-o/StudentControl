package com.maestrx.studentcontrol.studentapp.domain.wifi

import android.util.Log
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import javax.inject.Inject

@ViewModelScoped
class ServerInteractor @Inject constructor(
    private val wifiHelper: WifiHelper,
) {

    private val serverPort = 5951
    private lateinit var socket: DatagramSocket

    suspend fun dataExchange() = withContext(Dispatchers.IO) {
        closeSocket()
        socket = DatagramSocket(serverPort)
        Log.d("StudentApp", "Socket is open")
        val serverAddress = wifiHelper.getGatewayIpAddress()

        val sendData =
            "11111111111111111111111111111111111111111111111".toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
        socket.send(sendPacket)

        // Ожидание ACK от сервера
        val buffer = ByteArray(1024)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        socket.receive(receivePacket)

        val receivedData = String(receivePacket.data, 0, receivePacket.length)
        Log.d("StudentApp", "Received ACK from server: $receivedData")
    }

    fun closeSocket() {
        if (::socket.isInitialized && socket.isBound) {
            socket.close()
            Log.d("StudentApp", "Socket closed")
        }
    }
}