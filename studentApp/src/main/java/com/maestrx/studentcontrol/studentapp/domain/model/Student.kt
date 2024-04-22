package com.maestrx.studentcontrol.studentapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
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
) : Parcelable {
    val fullName
        get() = "$lastName $firstName $midName"
}