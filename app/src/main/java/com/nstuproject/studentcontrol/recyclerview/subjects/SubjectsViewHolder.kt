package com.nstuproject.studentcontrol.recyclerview.subjects

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nstuproject.studentcontrol.databinding.CardSubjectBinding
import com.nstuproject.studentcontrol.model.Subject

class SubjectsViewHolder(
    private val binding: CardSubjectBinding
) : ViewHolder(binding.root) {

    fun bind(item: Subject) = with(binding) {
        subject.text = item.name
    }
}