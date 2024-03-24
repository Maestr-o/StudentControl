package com.nstuproject.studentcontrol.model

import com.nstuproject.studentcontrol.db.entity.LessonGroupCrossRefEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonGroupCrossRef(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("lessonId")
    val lessonId: Long = 0L,
    @SerialName("groupId")
    val groupId: Long = 0L,
) {
    fun toEntity() = LessonGroupCrossRefEntity(id, lessonId, groupId)
}