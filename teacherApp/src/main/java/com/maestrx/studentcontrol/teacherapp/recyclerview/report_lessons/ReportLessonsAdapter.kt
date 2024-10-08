package com.maestrx.studentcontrol.teacherapp.recyclerview.report_lessons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.model.ReportLesson
import com.maestrx.studentcontrol.teacherapp.util.DatePreferenceManager

class ReportLessonsAdapter(
    private val dateManager: DatePreferenceManager,
) : RecyclerView.Adapter<ReportLessonViewHolder>() {

    var items: List<ReportLesson> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportLessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_table_item, parent, false)
        return ReportLessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportLessonViewHolder, position: Int) {
        holder.bind(items[position], dateManager.getDate())
    }

    override fun getItemCount(): Int = items.count()

    fun updateList(newList: List<ReportLesson>) {
        items = newList
        notifyDataSetChanged()
    }
}