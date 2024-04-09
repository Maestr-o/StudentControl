package com.maestrx.studentcontrol.teacherapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonResponse(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("title")
    val title: String = "",
    @SerialName("timeStart")
    val timeStart: Long = 0L,
    @SerialName("timeEnd")
    val timeEnd: Long = 0L,
    @SerialName("subjectId")
    val subjectId: Long = 0L,
    @SerialName("subjectName")
    val subjectName: String = "",
    @SerialName("auditory")
    val auditory: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("type")
    val type: LessonType = LessonType.LECTURE,
)