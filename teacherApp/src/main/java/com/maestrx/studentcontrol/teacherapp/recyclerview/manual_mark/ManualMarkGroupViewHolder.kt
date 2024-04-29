package com.maestrx.studentcontrol.teacherapp.recyclerview.manual_mark

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardMarkGroupBinding
import com.maestrx.studentcontrol.teacherapp.model.Group

class ManualMarkGroupViewHolder(
    private val binding: CardMarkGroupBinding,
) : ViewHolder(binding.root) {

    fun bind(item: Group) {
        binding.apply {
            group.text = item.name
        }
    }
}