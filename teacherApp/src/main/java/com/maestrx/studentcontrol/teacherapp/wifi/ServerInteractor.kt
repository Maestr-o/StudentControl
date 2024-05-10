package com.maestrx.studentcontrol.teacherapp.wifi

import android.util.Log
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.repository.attendance.AttendanceRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import javax.inject.Inject

@ViewModelScoped
class ServerInteractor @Inject constructor(
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
) {

    private val defaultServerPort = 5951
    private lateinit var socket: DatagramSocket

    suspend fun dataExchange(lesson: Lesson) = withContext(Dispatchers.IO) {
        closeSocket()
        socket = DatagramSocket(defaultServerPort)
        Log.d(Constants.DEBUG_TAG, "Main socket is open, port: ${socket.localPort}")
        coroutineScope {
            while (true) {
                val packetDeviceId = receive()
                launch {
                    try {
                        val studentHandler =
                            StudentDataHandler(studentRepository, attendanceRepository, lesson)
                        studentHandler.handleStudentData(packetDeviceId)
                    } catch (e: Exception) {
                        Log.d(Constants.DEBUG_TAG, "Error: $e")
                    }
                }
            }
        }
    }

    private fun receive(): DatagramPacket {
        val buffer = ByteArray(1024)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        socket.receive(receivePacket)
        return receivePacket
    }

    fun closeSocket() {
        if (::socket.isInitialized && socket.isBound) {
            socket.close()
            Log.d(Constants.DEBUG_TAG, "Main socket closed")
        }
    }
}