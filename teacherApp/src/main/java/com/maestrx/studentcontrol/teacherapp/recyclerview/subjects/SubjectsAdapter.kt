package com.maestrx.studentcontrol.teacherapp.recyclerview.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.maestrx.studentcontrol.teacherapp.databinding.CardSubjectBinding
import com.maestrx.studentcontrol.teacherapp.model.Subject

class SubjectsAdapter(
    private val listener: SubjectsListener,
) : ListAdapter<Subject, SubjectsViewHolder>(StudentsDiffCallback()) {

    interface SubjectsListener {
        fun onEditClickListener(subject: Subject)
        fun onDeleteClickListener(subject: Subject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardSubjectBinding.inflate(inflater, parent, false)
        val viewHolder = SubjectsViewHolder(binding)

        binding.edit.setOnClickListener {
            listener.onEditClickListener(getItem(viewHolder.adapterPosition))
        }

        binding.delete.setOnClickListener {
            listener.onDeleteClickListener(getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: SubjectsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}