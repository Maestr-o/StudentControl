package com.maestrx.studentcontrol.teacherapp.recyclerview.lessons

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.maestrx.studentcontrol.teacherapp.model.Lesson

class LessonsDiffCallback : ItemCallback<Lesson>() {
    override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean =
        oldItem == newItem
}