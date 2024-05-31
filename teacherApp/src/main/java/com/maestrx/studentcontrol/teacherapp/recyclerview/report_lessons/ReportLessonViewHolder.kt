package com.maestrx.studentcontrol.teacherapp.recyclerview.report_lessons

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.ReportTableItemBinding
import com.maestrx.studentcontrol.teacherapp.model.ReportLesson
import com.maestrx.studentcontrol.teacherapp.util.TimeFormatter

class ReportLessonViewHolder(
    private val view: View
) : RecyclerView.ViewHolder(view) {

    fun bind(reportLesson: ReportLesson, firstWeekTime: Long) {
        val binding = ReportTableItemBinding.bind(view)
        val time = reportLesson.lesson.timeStart
        val week = TimeFormatter.getWeekNumberFromDate(firstWeekTime, time)
        val str = "$week ${view.context.getString(R.string.week)}, ${
            TimeFormatter.unixTimeToDayOfWeek(time)
        } - ${TimeFormatter.unixTimeToDateShortYearString(time)}"
        binding.apply {
            date.text = str
            mark.isChecked = reportLesson.isMarked

            mark.setOnClickListener {
                reportLesson.isMarked = !reportLesson.isMarked
            }
        }
    }
}