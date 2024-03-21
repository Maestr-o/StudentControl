package com.nstuproject.studentcontrol.recyclerview.grooupsChoose

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.CardGroupChooseBinding
import com.nstuproject.studentcontrol.model.Group

class GroupsChooseViewHolder(
    private val binding: CardGroupChooseBinding
) : ViewHolder(binding.root) {

    fun bind(item: Group, isSelected: Boolean) {
        binding.category.text = item.name
        if (isSelected) {
            binding.card.setBackgroundResource(R.drawable.card_group_choose_background)
        } else {
            binding.card.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}