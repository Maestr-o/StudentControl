package com.nstuproject.studentcontrol.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonType
import java.time.Instant

@Entity(
    tableName = "Lesson",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subjectId"),
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class LessonEntity(
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "title")
    val title: String? = null,
    @ColumnInfo(name = "time")
    val time: Instant = Instant.now(),
    @ColumnInfo(name = "subjectId")
    val subjectId: Long = 0L,
    @ColumnInfo(name = "auditory")
    val auditory: String? = null,
    @ColumnInfo(name = "description")
    val description: String? = null,
    @ColumnInfo(name = "type")
    val type: LessonType = LessonType.LECTURE,
) {

    companion object {
        fun toEntity(data: Lesson): LessonEntity =
            with(data) {
                LessonEntity(
                    id, title, time, subjectId, auditory, description, type
                )
            }
    }

    fun toData(): Lesson = Lesson(id, title, time, subjectId, auditory, description)
}