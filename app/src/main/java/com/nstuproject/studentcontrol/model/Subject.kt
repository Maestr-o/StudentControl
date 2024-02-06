package com.nstuproject.studentcontrol.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("name")
    val name: String = "",
)