package com.nstuproject.studentcontrol.recyclerview.lessons

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.nstuproject.studentcontrol.model.Lesson

class LessonsDiffCallback : ItemCallback<Lesson>() {
    override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean =
        oldItem == newItem
}