package com.nstuproject.studentcontrol.model

import com.nstuproject.studentcontrol.db.entity.LessonEntity
import com.nstuproject.studentcontrol.utils.TimeFormatter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("title")
    val title: String = "",
    @SerialName("date")
    val date: String = "",
    @SerialName("time")
    val time: String = "",
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
                date = TimeFormatter.unixTimeToDateString(response.datetime),
                time = TimeFormatter.unixTimeToTimeString(response.datetime),
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
        time = TimeFormatter.stringToUnixTime(date, time),
        subjectId = subject.id,
        auditory = auditory,
        description = description,
        type = type,
    )
}