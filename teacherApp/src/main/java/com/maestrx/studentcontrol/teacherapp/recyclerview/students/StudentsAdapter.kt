package com.maestrx.studentcontrol.teacherapp.recyclerview.students

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.maestrx.studentcontrol.teacherapp.databinding.CardStudentBinding
import com.maestrx.studentcontrol.teacherapp.model.Student

class StudentsAdapter(
    private val listener: StudentsListener,
) : ListAdapter<Student, StudentsViewHolder>(StudentsDiffCallback()) {

    interface StudentsListener {
        fun onEditClickListener(view: View, student: Student)
        fun onDeleteClickListener(view: View, student: Student)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardStudentBinding.inflate(inflater, parent, false)
        val viewHolder = StudentsViewHolder(binding)

        binding.edit.setOnClickListener {
            listener.onEditClickListener(it, getItem(viewHolder.adapterPosition))
        }

        binding.delete.setOnClickListener {
            listener.onDeleteClickListener(it, getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}