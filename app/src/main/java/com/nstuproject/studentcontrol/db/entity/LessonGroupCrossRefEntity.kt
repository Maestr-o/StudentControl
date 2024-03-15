package com.nstuproject.studentcontrol.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nstuproject.studentcontrol.model.LessonGroupCrossRef

@Entity(
    tableName = "LessonGroupCrossRef",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lessonId"),
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("groupId"),
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class LessonGroupCrossRefEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "lessonId")
    val lessonId: Long = 0L,
    @ColumnInfo(name = "groupId")
    val groupId: Long = 0L,
) {

    companion object {
        fun toEntity(data: LessonGroupCrossRef) =
            with(data) {
                LessonGroupCrossRefEntity(id, lessonId, groupId)
            }
    }

    fun toData(): LessonGroupCrossRefEntity = LessonGroupCrossRefEntity(id, lessonId, groupId)
}