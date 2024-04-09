package com.maestrx.studentcontrol.teacherapp.recyclerview.groupsSelected

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardGroupSelectBinding
import com.maestrx.studentcontrol.teacherapp.model.Group

class GroupsSelectedViewHolder(
    private val binding: CardGroupSelectBinding
) : ViewHolder(binding.root) {

    fun bind(item: Group) {
        binding.group.text = item.name
    }
}