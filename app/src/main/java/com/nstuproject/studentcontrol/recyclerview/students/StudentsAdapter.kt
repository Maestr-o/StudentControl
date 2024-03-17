package com.nstuproject.studentcontrol.recyclerview.students

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.nstuproject.studentcontrol.databinding.CardStudentBinding
import com.nstuproject.studentcontrol.model.Student

class StudentsAdapter(
    private val listener: StudentsListener,
) : ListAdapter<Student, StudentsViewHolder>(StudentsDiffCallback()) {

    interface StudentsListener {
        fun onEditClickListener(student: Student)
        fun onDeleteClickListener(student: Student)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardStudentBinding.inflate(inflater, parent, false)
        val viewHolder = StudentsViewHolder(binding)

        binding.edit.setOnClickListener {
            listener.onEditClickListener(getItem(viewHolder.adapterPosition))
        }

        binding.delete.setOnClickListener {
            listener.onDeleteClickListener(getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}