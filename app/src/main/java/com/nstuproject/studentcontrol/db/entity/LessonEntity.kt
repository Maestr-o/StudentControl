package com.nstuproject.studentcontrol.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonType

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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "title")
    val title: String? = null,
    @ColumnInfo(name = "time")
    val time: String = "",
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
                LessonEntity(id, title, time, subject.id, auditory, description, type)
            }
    }
}