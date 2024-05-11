package com.maestrx.studentcontrol.teacherapp.recyclerview.marked_students

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.databinding.CardRegisteredGroupBinding
import com.maestrx.studentcontrol.teacherapp.databinding.CardRegisteredStudentBinding
import com.maestrx.studentcontrol.teacherapp.model.MarkInGroup
import com.maestrx.studentcontrol.teacherapp.model.Student

class MarkedStudentsAdapter(
    private var list: List<Any>
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_GROUP = 1
        private const val VIEW_TYPE_STUDENT = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_GROUP -> {
                val binding = CardRegisteredGroupBinding.inflate(inflater, parent, false)
                MarkedGroupViewHolder(binding)
            }

            VIEW_TYPE_STUDENT -> {
                val binding = CardRegisteredStudentBinding.inflate(inflater, parent, false)
                MarkedStudentViewHolder(binding)
            }

            else -> throw IllegalStateException("Invalid item type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is MarkedGroupViewHolder -> holder.bind(list[position] as MarkInGroup)
            is MarkedStudentViewHolder -> holder.bind(list[position] as Student)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int =
        when (list[position]) {
            is MarkInGroup -> VIEW_TYPE_GROUP
            is Student -> VIEW_TYPE_STUDENT
            else -> throw IllegalStateException("Invalid item type")
        }

    fun updateData(newList: List<Any>) {
        val diffCallback = MarkedStudentsDiffCallback(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }
}