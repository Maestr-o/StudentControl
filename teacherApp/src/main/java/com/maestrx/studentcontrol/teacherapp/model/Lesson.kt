package com.maestrx.studentcontrol.teacherapp.model

import com.maestrx.studentcontrol.teacherapp.db.entity.LessonEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("title")
    val title: String = "",
    @SerialName("timeStart")
    val timeStart: Long = 0L,
    @SerialName("timeEnd")
    val timeEnd: Long = 0L,
    @SerialName("subject")
    val subject: Subject = Subject(),
    @SerialName("auditory")
    val auditory: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("type")
    val type: LessonType = LessonType.LECTURE,
    @SerialName("groups")
    val groups: List<Group> = emptyList(),
) {
    companion object {
        fun fromResponseToData(response: LessonResponse) = with(response) {
            Lesson(
                id = id,
                title = title,
                timeStart = timeStart,
                timeEnd = timeEnd,
                subject = Subject(subjectId, subjectName),
                auditory = auditory,
                description = description,
                type = type,
            )
        }
    }

    fun toEntity() = LessonEntity(
        id = id,
        title = title,
        timeStart = timeStart,
        timeEnd = timeEnd,
        subjectId = subject.id,
        auditory = auditory,
        description = description,
        type = type,
    )
}