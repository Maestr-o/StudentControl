package com.nstuproject.studentcontrol.recyclerview.groupsSelected

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.nstuproject.studentcontrol.databinding.CardGroupSelectBinding
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.recyclerview.groups.GroupsDiffCallback

class GroupSelectedAdapter :
    ListAdapter<Group, GroupsSelectedViewHolder>(GroupsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsSelectedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardGroupSelectBinding.inflate(inflater, parent, false)

        return GroupsSelectedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupsSelectedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}