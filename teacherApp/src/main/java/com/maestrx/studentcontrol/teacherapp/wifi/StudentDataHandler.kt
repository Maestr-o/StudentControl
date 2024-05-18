package com.maestrx.studentcontrol.teacherapp.wifi

import android.util.Log
import com.maestrx.studentcontrol.teacherapp.db.entity.MarkEntity
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import com.maestrx.studentcontrol.teacherapp.repository.mark.MarkRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.util.Constants
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
    private val markRepository: MarkRepository,
    private val lesson: Lesson,
) {

    val newSocket = DatagramSocket()

    suspend fun handleStudentData(packetDeviceId: DatagramPacket) = withContext(Dispatchers.IO) {
        try {
            val deviceId = extractData(packetDeviceId)
            val studentId = studentRepository.getIdByDeviceId(deviceId)
            val students = getStudents(lesson)
            val studentsIds = students.map { student ->
                student.id
            }

            if (studentId != 0L && studentsIds.contains(studentId)) {
                saveMark(lesson.id, studentId)
                send(packetDeviceId.address, packetDeviceId.port, "ACK$deviceId")
            } else {
                send(
                    packetDeviceId.address,
                    packetDeviceId.port,
                    Json.encodeToString(ListSerializer(Student.serializer()), students)
                )
                val newStudentId = extractData(receive()).toLong()
                studentRepository.saveDeviceId(newStudentId, deviceId)
                saveMark(lesson.id, newStudentId)
                send(packetDeviceId.address, packetDeviceId.port, "ACK2$deviceId")
            }
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_TAG, "Exchanging data error: $e")
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
        try {
            newSocket.soTimeout = Constants.TIMEOUT
            newSocket.receive(receivePacket)
            return receivePacket
        } catch (e: SocketTimeoutException) {
            throw Exception("Receive operation timed out", e)
        }
    }

    private fun extractData(packet: DatagramPacket): String {
        val data = String(packet.data, 0, packet.length)
        Log.d(Constants.DEBUG_TAG, "Received data: $data")
        return data
    }

    private suspend fun saveMark(lessonId: Long, studentId: Long) {
        markRepository.save(
            MarkEntity(
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
        if (newSocket.isBound) {
            newSocket.close()
            Log.d(Constants.DEBUG_TAG, "Socket closed")
        }
    }
}