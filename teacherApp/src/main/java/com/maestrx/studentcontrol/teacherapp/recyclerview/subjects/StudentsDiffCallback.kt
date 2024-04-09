package com.maestrx.studentcontrol.teacherapp.recyclerview.subjects

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.maestrx.studentcontrol.teacherapp.model.Subject

class StudentsDiffCallback : ItemCallback<Subject>() {
    override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean =
        oldItem == newItem
}