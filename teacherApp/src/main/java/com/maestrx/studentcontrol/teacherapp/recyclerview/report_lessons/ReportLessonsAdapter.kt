package com.maestrx.studentcontrol.teacherapp.recyclerview.report_lessons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maestrx.studentcontrol.teacherapp.databinding.ReportTableItemBinding
import com.maestrx.studentcontrol.teacherapp.model.ReportLesson

class ReportLessonsAdapter : RecyclerView.Adapter<ReportLessonViewHolder>() {

    private var list: List<ReportLesson> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportLessonViewHolder {
        val binding = ReportTableItemBinding.inflate(LayoutInflater.from(parent.context))
        return ReportLessonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportLessonViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.count()

    fun setList(newList: List<ReportLesson>) {
        list = newList
        notifyDataSetChanged()
    }
}