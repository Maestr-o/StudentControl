package com.nstuproject.studentcontrol.model

import com.nstuproject.studentcontrol.utils.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Lesson(
    @SerialName("id")
    val id: Long = 0L,
    @Serializable(InstantSerializer::class)
    @SerialName("date")
    val date: Instant = Instant.now(),
    @SerialName("type")
    val type: LessonType = LessonType.LECTURE,
    @SerialName("theme")
    val theme: String? = null,
    @SerialName("subjectId")
    val subjectId: Long = 0L,
    @SerialName("groupId")
    val groupId: Long = 0L,
    @SerialName("auditory")
    val auditory: String? = null,
)