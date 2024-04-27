package com.maestrx.studentcontrol.teacherapp.model

import com.maestrx.studentcontrol.teacherapp.db.entity.LessonGroupCrossEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonGroupCross(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("lessonId")
    val lessonId: Long = 0L,
    @SerialName("groupId")
    val groupId: Long = 0L,
) {
    fun toEntity() = LessonGroupCrossEntity(id, lessonId, groupId)
}