package com.nstuproject.studentcontrol.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Student(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("midName")
    val midName: String? = null,
    @SerialName("lastName")
    val lastName: String = "",
)