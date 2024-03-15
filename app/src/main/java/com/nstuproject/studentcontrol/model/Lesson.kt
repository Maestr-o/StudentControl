package com.nstuproject.studentcontrol.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("title")
    val title: String? = null,
    @SerialName("time")
    val time: String = "",
    @SerialName("subjectId")
    val subjectId: Long = 0L,
    @SerialName("auditory")
    val auditory: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("type")
    val type: LessonType = LessonType.LECTURE,
)