package com.nstuproject.studentcontrol.model

import com.nstuproject.studentcontrol.db.entity.StudentEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Student(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("group")
    val group: Group = Group(),
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("midName")
    val midName: String? = null,
    @SerialName("lastName")
    val lastName: String = "",
    @SerialName("deviceId")
    val deviceId: String = "",
) {
    companion object {
        fun fromResponseToData(entity: StudentResponse) = with(entity) {
            Student(id, Group(groupId, groupName), firstName, midName, lastName, deviceId)
        }
    }

    val fullName
        get() = "$lastName $firstName $midName"

    fun toEntity() = StudentEntity(id, group.id, firstName, midName, lastName, deviceId)
}