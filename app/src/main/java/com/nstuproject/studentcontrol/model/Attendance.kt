package com.nstuproject.studentcontrol.model

import com.nstuproject.studentcontrol.db.entity.AttendanceEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attendance(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("lessonId")
    val lessonId: Long = 0L,
    @SerialName("studentId")
    val studentId: Long = 0L,
    @SerialName("attended")
    val attended: Boolean = false,
) {
    companion object {
        fun toData(entity: AttendanceEntity) = with(entity) {
            Attendance(id, lessonId, studentId, attended)
        }
    }

    fun toEntity() = AttendanceEntity(id, lessonId, studentId, attended)
}