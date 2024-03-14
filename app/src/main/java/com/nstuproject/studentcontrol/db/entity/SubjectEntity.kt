package com.nstuproject.studentcontrol.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nstuproject.studentcontrol.model.Subject

@Entity(tableName = "Subject")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,
    @ColumnInfo(name = "name")
    val name: String = "",
) {

    companion object {
        fun toEntity(data: Subject): SubjectEntity =
            with(data) {
                SubjectEntity(id, name)
            }
    }

    fun toData(): Subject = Subject(id, name)
}