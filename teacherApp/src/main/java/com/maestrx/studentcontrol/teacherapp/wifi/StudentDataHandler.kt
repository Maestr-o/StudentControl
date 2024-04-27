package com.maestrx.studentcontrol.teacherapp.wifi

import android.util.Log
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import com.maestrx.studentcontrol.teacherapp.repository.attendance.AttendanceRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

class StudentDataHandler(
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val lesson: Lesson,
) {

    private lateinit var newSocket: DatagramSocket

    suspend fun handleStudentData(packetDeviceId: DatagramPacket) = withContext(Dispatchers.IO) {
        val deviceId = getData(packetDeviceId)
        val studentId = studentRepository.getIdByDeviceId(deviceId)

        newSocket = DatagramSocket()
        Log.d(Constants.DEBUG_TAG, "Socket is open, port: ${newSocket.localPort}")

        if (studentId != 0L) {
            saveAttendance(lesson.id, studentId)
            send(packetDeviceId.address, packetDeviceId.port, "ACK$deviceId")
        } else {
            val data = getStudents(lesson)
            send(
                packetDeviceId.address,
                packetDeviceId.port,
                Json.encodeToString(
                    ListSerializer(Student.serializer()),
                    data
                )
            )
            val newStudentId = getData(receive()).toLong()
            studentRepository.saveDeviceId(newStudentId, deviceId)
            saveAttendance(lesson.id, newStudentId)
            send(packetDeviceId.address, packetDeviceId.port, "ACK2$deviceId")
        }

        closeSocket()
    }

    private fun send(studentAddress: InetAddress, studentPort: Int, message: String) {
        val sendData = message.toByteArray()
        val sendPacket = DatagramPacket(sendData, sendData.size, studentAddress, studentPort)
        newSocket.send(sendPacket)
        Log.d(Constants.DEBUG_TAG, "Sent data to ${sendPacket.address}:$studentPort: $message")
    }

    private fun receive(): DatagramPacket {
        val buffer = ByteArray(1024)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        newSocket.soTimeout = Constants.TIMEOUT
        try {
            newSocket.receive(receivePacket)
            return receivePacket
        } catch (e: SocketTimeoutException) {
            closeSocket()
            throw Exception("Receive operation timed out", e)
        }
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
            students += studentRepository.getByGroupId(groupId).first()
        }
        return students.map {
            Student.fromResponseToData(it)
        }
    }

    private fun closeSocket() {
        if (::newSocket.isInitialized && newSocket.isBound) {
            newSocket.close()
            Log.d(Constants.DEBUG_TAG, "Socket closed")
        }
    }
}