package com.maestrx.studentcontrol.teacherapp.recyclerview.manual_mark

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardMarkGroupBinding
import com.maestrx.studentcontrol.teacherapp.model.Group

class ManualMarkGroupViewHolder(
    private val view: View
) : ViewHolder(view) {

    fun bind(item: Group) {
        val binding = CardMarkGroupBinding.bind(view)
        binding.apply {
            group.text = item.name
        }
    }
}