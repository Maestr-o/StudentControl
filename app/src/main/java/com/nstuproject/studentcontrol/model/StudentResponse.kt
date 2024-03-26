package com.nstuproject.studentcontrol.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentResponse(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("groupId")
    val groupId: Long = 0L,
    @SerialName("groupName")
    val groupName: String = "",
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("midName")
    val midName: String? = null,
    @SerialName("lastName")
    val lastName: String = "",
    @SerialName("deviceId")
    val deviceId: String = "",
)