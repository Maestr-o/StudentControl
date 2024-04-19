package com.maestrx.studentcontrol.teacherapp.recyclerview.attended_students

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardRegisteredStudentBinding
import com.maestrx.studentcontrol.teacherapp.model.Student

class AttendedStudentViewHolder(
    private val binding: CardRegisteredStudentBinding
) : ViewHolder(binding.root) {

    fun bind(student: Student) {
        binding.apply {
            name.text = student.fullName
        }
    }
}