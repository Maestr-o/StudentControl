package com.nstuproject.studentcontrol.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonGroupCrossRef(
    @SerialName("lessonId")
    val lessonId: Long = 0L,
    @SerialName("groupId")
    val groupId: Long = 0L,
)