package com.nstuproject.studentcontrol.recyclerview.students

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.nstuproject.studentcontrol.model.Student

class StudentsDiffCallback : ItemCallback<Student>() {
    override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
        oldItem == newItem
}