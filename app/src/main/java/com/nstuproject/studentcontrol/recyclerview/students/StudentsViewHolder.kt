package com.nstuproject.studentcontrol.recyclerview.students

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.CardStudentBinding
import com.nstuproject.studentcontrol.model.Student

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