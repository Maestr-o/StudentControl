package com.maestrx.studentcontrol.teacherapp.recyclerview.report_lessons

import androidx.recyclerview.widget.RecyclerView
import com.maestrx.studentcontrol.teacherapp.databinding.ReportTableItemBinding
import com.maestrx.studentcontrol.teacherapp.model.ReportLesson
import com.maestrx.studentcontrol.teacherapp.utils.TimeFormatter

class ReportLessonViewHolder(
    private val binding: ReportTableItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(reportLesson: ReportLesson) {
        binding.apply {
            date.text = TimeFormatter.unixTimeToDateString(reportLesson.lesson.timeStart)
            title.text = reportLesson.lesson.title
            mark.isChecked = reportLesson.isMarked
        }
    }
}