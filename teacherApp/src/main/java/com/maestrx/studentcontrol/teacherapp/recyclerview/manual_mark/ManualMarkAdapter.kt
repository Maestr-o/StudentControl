package com.maestrx.studentcontrol.teacherapp.recyclerview.manual_mark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.StudentMark

class ManualMarkAdapter(
    val items: List<Any>,
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val STUDENT_TYPE = 1
        const val GROUP_TYPE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            STUDENT_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_mark_student, parent, false)
                ManualMarkStudentViewHolder(view)
            }

            GROUP_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_mark_group, parent, false)
                ManualMarkGroupViewHolder(view)
            }

            else -> throw IllegalStateException("Invalid item type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ManualMarkStudentViewHolder -> {
                holder.bind(items[position] as StudentMark)
            }

            is ManualMarkGroupViewHolder -> {
                holder.bind(items[position] as Group)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is StudentMark -> STUDENT_TYPE
            is Group -> GROUP_TYPE
            else -> throw throw IllegalStateException("Invalid item type")
        }
}