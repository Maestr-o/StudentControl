package com.nstuproject.studentcontrol.recyclerview.groups

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.nstuproject.studentcontrol.model.Group

class GroupsDiffCallback : ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean =
        oldItem == newItem
}