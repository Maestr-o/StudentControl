package com.nstuproject.studentcontrol.db.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonGroupCrossRefEntity(
    @SerialName("lessonId")
    val lessonId: Long = 0L,
    @SerialName("groupId")
    val groupId: Long = 0L,
)