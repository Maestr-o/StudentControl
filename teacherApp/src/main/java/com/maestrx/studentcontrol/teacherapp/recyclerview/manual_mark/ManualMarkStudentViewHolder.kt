package com.maestrx.studentcontrol.teacherapp.recyclerview.manual_mark

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardMarkStudentBinding
import com.maestrx.studentcontrol.teacherapp.model.StudentMark

class ManualMarkStudentViewHolder(
    private val binding: CardMarkStudentBinding
) : ViewHolder(binding.root) {

    fun bind(item: StudentMark) {
        binding.apply {
            name.text = item.fullName

            mark.setOnCheckedChangeListener { _, isChecked ->
                item.isAttended = isChecked
            }
        }
    }
}