package com.maestrx.studentcontrol.teacherapp.recyclerview.subjects

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardSubjectBinding
import com.maestrx.studentcontrol.teacherapp.model.Subject

class SubjectsViewHolder(
    private val binding: CardSubjectBinding
) : ViewHolder(binding.root) {

    fun bind(item: Subject) = with(binding) {
        subject.text = item.name
    }
}