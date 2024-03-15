package com.nstuproject.studentcontrol.recyclerview.subjects

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.nstuproject.studentcontrol.model.Subject

class StudentsDiffCallback : ItemCallback<Subject>() {
    override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean =
        oldItem == newItem
}