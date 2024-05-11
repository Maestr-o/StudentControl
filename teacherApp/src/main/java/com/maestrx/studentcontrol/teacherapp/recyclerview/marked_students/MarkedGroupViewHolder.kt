package com.maestrx.studentcontrol.teacherapp.recyclerview.marked_students

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.CardRegisteredGroupBinding
import com.maestrx.studentcontrol.teacherapp.model.MarkInGroup

class MarkedGroupViewHolder(
    private val binding: CardRegisteredGroupBinding
) : ViewHolder(binding.root) {

    fun bind(item: MarkInGroup) {
        binding.apply {
            group.text = item.name
            count.text =
                root.context.getString(R.string.student_count_placeholder, item.count, item.max)
        }
    }
}