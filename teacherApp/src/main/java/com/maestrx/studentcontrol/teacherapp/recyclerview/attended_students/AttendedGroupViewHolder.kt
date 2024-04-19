package com.maestrx.studentcontrol.teacherapp.recyclerview.attended_students

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardRegisteredGroupBinding
import com.maestrx.studentcontrol.teacherapp.model.AttendedInGroup

class AttendedGroupViewHolder(
    private val binding: CardRegisteredGroupBinding
) : ViewHolder(binding.root) {

    fun bind(item: AttendedInGroup) {
        binding.apply {
            group.text = item.name
            count.text = item.count.toString()
        }
    }
}