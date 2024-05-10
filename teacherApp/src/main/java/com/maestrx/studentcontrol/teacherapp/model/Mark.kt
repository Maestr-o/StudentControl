package com.maestrx.studentcontrol.teacherapp.model

import com.maestrx.studentcontrol.teacherapp.db.entity.MarkEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mark(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("lessonId")
    val lessonId: Long = 0L,
    @SerialName("studentId")
    val studentId: Long = 0L,
) {
    companion object {
        fun toData(entity: MarkEntity) = with(entity) {
            Mark(id, lessonId, studentId)
        }
    }

    fun toEntity() = MarkEntity(id, lessonId, studentId)
}