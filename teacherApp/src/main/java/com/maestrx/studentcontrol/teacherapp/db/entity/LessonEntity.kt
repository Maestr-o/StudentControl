package com.maestrx.studentcontrol.teacherapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.maestrx.studentcontrol.teacherapp.model.LessonType

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
    val title: String = "",
    @ColumnInfo(name = "timeStart")
    val timeStart: Long = 0L,
    @ColumnInfo(name = "timeEnd")
    val timeEnd: Long = 0L,
    @ColumnInfo(name = "subjectId")
    val subjectId: Long = 0L,
    @ColumnInfo(name = "auditory")
    val auditory: String = "",
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "type")
    val type: LessonType = LessonType.LECTURE,
)