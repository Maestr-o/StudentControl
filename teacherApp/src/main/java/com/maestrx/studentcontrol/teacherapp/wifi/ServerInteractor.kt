package com.maestrx.studentcontrol.teacherapp.wifi

import android.util.Log
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import javax.inject.Inject

@ViewModelScoped
class ServerInteractor @Inject constructor() {

    private val serverPort = 5951
    private lateinit var socket: DatagramSocket

    suspend fun dataExchange() = withContext(Dispatchers.IO) {
        closeSocket()
        socket = DatagramSocket(serverPort)
        Log.d("TeacherApp", "Socket is open")
        while (true) {
            val buffer = ByteArray(1024)
            val receivePacket = DatagramPacket(buffer, buffer.size)
            socket.receive(receivePacket) // Получение данных от студента
            val receivedData = String(receivePacket.data, 0, receivePacket.length)
            Log.d("TeacherApp", "Received data from student: $receivedData")

            // Отправка ACK обратно студенту
            val sendData = "ACK".toByteArray()
            val studentAddress = receivePacket.address
            val studentPort = receivePacket.port
            val sendPacket =
                DatagramPacket(sendData, sendData.size, studentAddress, studentPort)
            socket.send(sendPacket)
            Log.d("TeacherApp", "Sent ACK to student")
        }
    }

    fun closeSocket() {
        if (::socket.isInitialized && socket.isBound) {
            socket.close()
            Log.d("TeacherApp", "Socket closed")
        }
    }
}