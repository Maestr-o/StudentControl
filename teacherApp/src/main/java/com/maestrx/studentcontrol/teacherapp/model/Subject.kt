package com.maestrx.studentcontrol.teacherapp.model

import com.maestrx.studentcontrol.teacherapp.db.entity.SubjectEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("name")
    val name: String = "",
) {
    companion object {
        fun toData(entity: SubjectEntity) = with(entity) {
            Subject(id, name)
        }
    }

    fun toEntity() = SubjectEntity(id, name)
}