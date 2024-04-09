package com.maestrx.studentcontrol.teacherapp.recyclerview.groups

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardGroupBinding
import com.maestrx.studentcontrol.teacherapp.model.Group

class GroupsViewHolder(
    private val binding: CardGroupBinding
) : ViewHolder(binding.root) {

    fun bind(item: Group) = with(binding) {
        group.text = item.name
    }
}