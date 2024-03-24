package com.nstuproject.studentcontrol.recyclerview.groupsSelect

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.CardGroupSelectBinding
import com.nstuproject.studentcontrol.model.Group

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