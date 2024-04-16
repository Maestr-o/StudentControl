package com.maestrx.studentcontrol.teacherapp.wifi

import android.util.Log
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import com.maestrx.studentcontrol.teacherapp.repository.attendance.AttendanceRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

@ViewModelScoped
class ServerInteractor @Inject constructor(
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
) {

    private val serverPort = 5951
    private lateinit var socket: DatagramSocket

    suspend fun dataExchange(lesson: Lesson) = withContext(Dispatchers.IO) {
        closeSocket()
        socket = DatagramSocket(serverPort)
        Log.d(Constants.DEBUG_TAG, "Socket is open")
        while (true) {
            val packetDeviceId = receive()

            val studentAddress = packetDeviceId.address
            val studentPort = packetDeviceId.port

            val deviceId = getData(packetDeviceId)

            val studentId = studentRepository.getStudentIdByDeviceId(deviceId)
            if (studentId != 0L) {
                saveAttendance(lesson.id, studentId)
                send(studentAddress, studentPort, "ACK$deviceId")
            } else {
                val data = getStudents(lesson)
                send(
                    studentAddress,
                    studentPort,
                    Json.encodeToString(ListSerializer(Student.serializer()), data)
                )
                val newStudentId = getData(receive()).toLong()
                studentRepository.saveDeviceId(newStudentId, deviceId)
                saveAttendance(lesson.id, newStudentId)
                send(studentAddress, studentPort, "ACK2$deviceId")
            }
        }
    }

    private fun send(studentAddress: InetAddress, studentPort: Int, message: String) {
        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, studentAddress, studentPort)
        socket.send(sendPacket)
        Log.d(Constants.DEBUG_TAG, "Sent data: $message")
    }

    private fun receive(): DatagramPacket {
        val buffer = ByteArray(1024)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        socket.receive(receivePacket)
        return receivePacket
    }

    private fun getData(packet: DatagramPacket): String {
        val data = String(packet.data, 0, packet.length)
        Log.d(Constants.DEBUG_TAG, "Received data: $data")
        return data
    }

    private suspend fun saveAttendance(lessonId: Long, studentId: Long) {
        attendanceRepository.save(
            AttendanceEntity(
                lessonId = lessonId,
                studentId = studentId,
            )
        )
    }

    private suspend fun getStudents(lesson: Lesson): List<Student> {
        val groupIds = mutableListOf<Long>()
        lesson.groups.forEach { group ->
            groupIds += group.id
        }
        val students = mutableListOf<StudentResponse>()
        groupIds.forEach { groupId ->
            students += studentRepository.getStudentsByGroup(groupId).first()
        }
        return students.map {
            Student.fromResponseToData(it)
        }
    }

    fun closeSocket() {
        if (::socket.isInitialized && socket.isBound) {
            socket.close()
            Log.d(Constants.DEBUG_TAG, "Socket closed")
        }
    }
}