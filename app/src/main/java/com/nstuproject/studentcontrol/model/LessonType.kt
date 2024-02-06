package com.nstuproject.studentcontrol.model

import kotlinx.serialization.Serializable

@Serializable
enum class LessonType {
    LECTURE,
    PRACTICE,
    LAB
}