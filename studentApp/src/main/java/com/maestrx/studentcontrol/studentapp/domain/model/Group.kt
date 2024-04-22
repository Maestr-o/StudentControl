package com.maestrx.studentcontrol.studentapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Group(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("name")
    val name: String = "",
) : Parcelable