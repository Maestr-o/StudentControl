package com.maestrx.studentcontrol.studentapp.domain.model

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
    val fullName
        get() = "$lastName $firstName $midName"
}