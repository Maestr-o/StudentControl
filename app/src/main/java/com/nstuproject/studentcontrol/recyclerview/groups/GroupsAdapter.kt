package com.nstuproject.studentcontrol.recyclerview.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.nstuproject.studentcontrol.databinding.CardGroupBinding
import com.nstuproject.studentcontrol.model.Group

class GroupsAdapter(
    private val listener: GroupsListener,
) : ListAdapter<Group, GroupsViewHolder>(GroupsDiffCallback()) {

    interface GroupsListener {
        fun onEditClickListener(group: Group)
        fun onDeleteClickListener(group: Group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardGroupBinding.inflate(inflater, parent, false)
        val viewHolder = GroupsViewHolder(binding)

        binding.edit.setOnClickListener {
            listener.onEditClickListener(getItem(viewHolder.adapterPosition))
        }

        binding.delete.setOnClickListener {
            listener.onDeleteClickListener(getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}