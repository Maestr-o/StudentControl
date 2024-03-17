package com.nstuproject.studentcontrol.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nstuproject.studentcontrol.model.Student

@Entity(
    tableName = "Student",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("groupId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "groupId")
    val groupId: Long = 0L,
    @ColumnInfo(name = "firstName")
    val firstName: String = "",
    @ColumnInfo(name = "midName")
    val midName: String? = null,
    @ColumnInfo(name = "lastName")
    val lastName: String = "",
    @ColumnInfo(name = "deviceId")
    val deviceId: String = "",
) {

    companion object {
        fun toEntity(data: Student): StudentEntity = with(data) {
            StudentEntity(id, groupId, firstName, midName, lastName, deviceId)
        }
    }

    fun toData(): Student = Student(id, groupId, firstName, midName, lastName, deviceId)
}