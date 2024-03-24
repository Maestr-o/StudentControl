package com.nstuproject.studentcontrol.recyclerview.groupsSelected

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nstuproject.studentcontrol.databinding.CardGroupSelectBinding
import com.nstuproject.studentcontrol.model.Group

class GroupsSelectedViewHolder(
    private val binding: CardGroupSelectBinding
) : ViewHolder(binding.root) {

    fun bind(item: Group) {
        binding.group.text = item.name
    }
}