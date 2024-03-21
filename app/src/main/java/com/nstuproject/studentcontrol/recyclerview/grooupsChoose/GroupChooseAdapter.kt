package com.nstuproject.studentcontrol.recyclerview.grooupsChoose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.nstuproject.studentcontrol.databinding.CardGroupChooseBinding
import com.nstuproject.studentcontrol.model.Group

class GroupChooseAdapter(
    private val listener: GroupChooseListener,
    private var selectedItems: List<Int> = emptyList()
) : ListAdapter<Group, GroupsChooseViewHolder>(GroupsChooseItemCallback()) {

    interface GroupChooseListener {
        fun groupChooseClick(item: Group, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsChooseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardGroupChooseBinding.inflate(inflater, parent, false)
        val viewHolder = GroupsChooseViewHolder(binding)

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

    override fun onBindViewHolder(holder: GroupsChooseViewHolder, position: Int) {
        holder.bind(getItem(position), selectedItems.contains(position))
    }
}