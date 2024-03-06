package com.nstuproject.studentcontrol.model

import com.nstuproject.studentcontrol.utils.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Lesson(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("title")
    val title: String? = null,
    @Serializable(InstantSerializer::class)
    @SerialName("time")
    val time: Instant = Instant.now(),
    @SerialName("subjectId")
    val subjectId: Long = 0L,
    @SerialName("auditory")
    val auditory: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("type")
    val type: LessonType = LessonType.LECTURE,
)