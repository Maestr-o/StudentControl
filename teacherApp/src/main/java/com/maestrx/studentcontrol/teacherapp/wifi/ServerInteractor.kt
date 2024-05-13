package com.maestrx.studentcontrol.teacherapp.wifi

import android.util.Log
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.repository.attendance.MarkRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import javax.inject.Inject

@ViewModelScoped
class ServerInteractor @Inject constructor(
    private val studentRepository: StudentRepository,
    private val markRepository: MarkRepository,
) {

    private val defaultServerPort = 5951
    private lateinit var mainSocket: DatagramSocket

    private var loopJob: Job? = null
    private var sockets: List<DatagramSocket> = mutableListOf()

    suspend fun dataExchange(lesson: Lesson) = withContext(Dispatchers.IO) {
        closeSocket()
        mainSocket = DatagramSocket(defaultServerPort)
        Log.d(Constants.DEBUG_TAG, "Main socket is open, port: ${mainSocket.localPort}")
        coroutineScope {
            while (true) {
                val packetDeviceId = receive()
                loopJob = launch {
                    try {
                        val studentHandler =
                            StudentDataHandler(studentRepository, markRepository, lesson)
                        sockets += studentHandler.newSocket
                        studentHandler.handleStudentData(packetDeviceId)
                        sockets -= studentHandler.newSocket
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
        mainSocket.receive(receivePacket)
        return receivePacket
    }

    fun closeSocket() {
        if (::mainSocket.isInitialized && mainSocket.isBound) {
            sockets.map { socket ->
                socket.close()
            }
            loopJob?.cancel()
            mainSocket.close()
            Log.d(Constants.DEBUG_TAG, "Main socket closed")
        }
    }
}