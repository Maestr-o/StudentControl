package com.maestrx.studentcontrol.teacherapp.recyclerview.students

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.maestrx.studentcontrol.teacherapp.model.Student

class StudentsDiffCallback : ItemCallback<Student>() {
    override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
        oldItem == newItem
}