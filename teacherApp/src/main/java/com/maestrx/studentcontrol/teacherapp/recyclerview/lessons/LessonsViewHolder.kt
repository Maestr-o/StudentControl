package com.maestrx.studentcontrol.teacherapp.recyclerview.lessons

import android.annotation.SuppressLint
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.CardLessonBinding
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.LessonType
import com.maestrx.studentcontrol.teacherapp.util.TimeFormatter

class LessonsViewHolder(
    private val binding: CardLessonBinding
) : ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: Lesson) = with(binding) {
        subject.text = item.subject.name
        type.text = when (item.type.name) {
            LessonType.LECTURE.toString() -> {
                root.context.getString(R.string.lecture)
            }

            LessonType.PRACTICE.toString() -> {
                root.context.getString(R.string.practice)
            }

            LessonType.LAB.toString() -> {
                root.context.getString(R.string.laboratory_work)
            }

            else -> {
                root.context.getString(R.string.lesson)
            }
        }
        if (item.title.isNotBlank()) {
            title.isVisible = true
            title.text = item.title
        } else {
            title.isGone = true
        }
        time.text = "${TimeFormatter.unixTimeToTimeString(item.timeStart)} - ${
            TimeFormatter.unixTimeToTimeString(item.timeEnd)
        }"
        auditory.text = item.auditory
    }
}