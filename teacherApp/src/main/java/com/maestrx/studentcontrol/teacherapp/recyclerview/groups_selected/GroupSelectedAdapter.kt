package com.maestrx.studentcontrol.teacherapp.recyclerview.groups_selected

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.maestrx.studentcontrol.teacherapp.databinding.CardGroupSelectBinding
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.recyclerview.groups.GroupsDiffCallback

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