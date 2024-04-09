package com.maestrx.studentcontrol.teacherapp.model

import kotlinx.serialization.Serializable

@Serializable
enum class LessonType {
    LECTURE,
    PRACTICE,
    LAB
}