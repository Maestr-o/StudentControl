package com.maestrx.studentcontrol.teacherapp.model

import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("name")
    val name: String = "",
) {
    companion object {
        fun toData(entity: GroupEntity) = with(entity) {
            Group(id, name)
        }
    }

    fun toEntity() = GroupEntity(id, name)

    override fun toString(): String = name
}