package com.maestrx.studentcontrol.teacherapp.recyclerview.groupsSelect

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.CardGroupSelectBinding
import com.maestrx.studentcontrol.teacherapp.model.Group

class GroupsSelectViewHolder(
    private val binding: CardGroupSelectBinding
) : ViewHolder(binding.root) {

    fun bind(item: Group, isSelected: Boolean) {
        binding.group.text = item.name
        if (isSelected) {
            binding.card.setBackgroundResource(R.drawable.card_group_choose_background)
        } else {
            binding.card.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}