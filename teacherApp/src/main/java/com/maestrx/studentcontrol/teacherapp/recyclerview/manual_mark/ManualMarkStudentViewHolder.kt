package com.maestrx.studentcontrol.teacherapp.recyclerview.manual_mark

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardMarkStudentBinding
import com.maestrx.studentcontrol.teacherapp.model.StudentMark

class ManualMarkStudentViewHolder(
    private val view: View
) : ViewHolder(view) {

    fun bind(item: StudentMark) {
        val binding = CardMarkStudentBinding.bind(view)
        binding.apply {
            name.text = item.fullName
            mark.isChecked = item.isAttended

            layout.setOnClickListener {
                mark.isChecked = !mark.isChecked
                item.isAttended = !item.isAttended
            }

            mark.setOnClickListener {
                item.isAttended = !item.isAttended
            }
        }
    }
}