package com.maestrx.studentcontrol.teacherapp.recyclerview.marked_students

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardRegisteredStudentBinding
import com.maestrx.studentcontrol.teacherapp.model.Student

class MarkedStudentViewHolder(
    private val binding: CardRegisteredStudentBinding
) : ViewHolder(binding.root) {

    fun bind(student: Student) {
        binding.apply {
            name.text = student.fullName
        }
    }
}