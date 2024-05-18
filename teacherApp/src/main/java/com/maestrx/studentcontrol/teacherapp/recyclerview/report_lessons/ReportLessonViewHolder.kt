package com.maestrx.studentcontrol.teacherapp.recyclerview.report_lessons

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maestrx.studentcontrol.teacherapp.databinding.ReportTableItemBinding
import com.maestrx.studentcontrol.teacherapp.model.ReportLesson
import com.maestrx.studentcontrol.teacherapp.util.TimeFormatter

class ReportLessonViewHolder(
    private val view: View
) : RecyclerView.ViewHolder(view) {

    fun bind(reportLesson: ReportLesson) {
        val binding = ReportTableItemBinding.bind(view)
        binding.apply {
            date.text = TimeFormatter.unixTimeToDateShortYearString(reportLesson.lesson.timeStart)
            title.text = reportLesson.lesson.title
            mark.isChecked = reportLesson.isMarked

            mark.setOnClickListener {
                reportLesson.isMarked = !reportLesson.isMarked
            }
        }
    }
}