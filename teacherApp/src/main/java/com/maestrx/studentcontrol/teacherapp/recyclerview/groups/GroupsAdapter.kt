package com.maestrx.studentcontrol.teacherapp.recyclerview.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.maestrx.studentcontrol.teacherapp.databinding.CardGroupBinding
import com.maestrx.studentcontrol.teacherapp.model.Group

class GroupsAdapter(
    private val listener: GroupsListener,
) : ListAdapter<Group, GroupsViewHolder>(GroupsDiffCallback()) {

    interface GroupsListener {
        fun onClickListener(group: Group)
        fun onEditClickListener(view: View, group: Group)
        fun onDeleteClickListener(view: View, group: Group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardGroupBinding.inflate(inflater, parent, false)
        val viewHolder = GroupsViewHolder(binding)

        binding.root.setOnClickListener {
            listener.onClickListener(getItem(viewHolder.adapterPosition))
        }

        binding.edit.setOnClickListener {
            listener.onEditClickListener(it, getItem(viewHolder.adapterPosition))
        }

        binding.delete.setOnClickListener {
            listener.onDeleteClickListener(it, getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}