package com.maestrx.studentcontrol.teacherapp.recyclerview.students

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.CardStudentBinding
import com.maestrx.studentcontrol.teacherapp.model.Student

class StudentsViewHolder(
    private val binding: CardStudentBinding
) : ViewHolder(binding.root) {

    fun bind(item: Student) = with(binding) {
        student.text = item.fullName
        if (item.deviceId.isNotBlank()) {
            indicator.setBackgroundResource(R.color.registered)
        } else {
            indicator.setBackgroundResource(R.color.unregistered)
        }
    }
}