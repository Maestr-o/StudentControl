package com.nstuproject.studentcontrol.recyclerview.groups

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nstuproject.studentcontrol.databinding.CardGroupBinding
import com.nstuproject.studentcontrol.model.Group

class GroupsViewHolder(
    private val binding: CardGroupBinding
) : ViewHolder(binding.root) {

    fun bind(item: Group) = with(binding) {
        group.text = item.name
    }
}