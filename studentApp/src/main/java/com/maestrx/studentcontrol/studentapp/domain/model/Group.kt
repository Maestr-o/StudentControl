package com.maestrx.studentcontrol.studentapp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("name")
    val name: String = "",
)