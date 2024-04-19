package com.maestrx.studentcontrol.teacherapp.recyclerview.groups_select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.maestrx.studentcontrol.teacherapp.databinding.CardGroupSelectBinding
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.recyclerview.groups.GroupsDiffCallback

class GroupSelectAdapter(
    private val listener: GroupChooseListener,
    private var selectedItems: List<Int> = emptyList()
) : ListAdapter<Group, GroupsSelectViewHolder>(GroupsDiffCallback()) {

    interface GroupChooseListener {
        fun groupChooseClick(item: Group, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsSelectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardGroupSelectBinding.inflate(inflater, parent, false)
        val viewHolder = GroupsSelectViewHolder(binding)

        binding.root.setOnClickListener {
            val position = viewHolder.adapterPosition
            val item = getItem(position)
            listener.groupChooseClick(item, position)
        }

        return viewHolder
    }

    fun addItem(position: Int) {
        selectedItems += position
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        selectedItems -= position
        notifyItemChanged(position)
    }

    fun setItems(items: List<Int>) {
        selectedItems = items
    }

    override fun onBindViewHolder(holder: GroupsSelectViewHolder, position: Int) {
        holder.bind(getItem(position), selectedItems.contains(position))
    }
}