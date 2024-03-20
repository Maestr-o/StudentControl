package com.nstuproject.studentcontrol.recyclerview.lessons

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.CardLessonBinding
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonType

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
        title.text = item.title
        time.text = "${item.timeStart} - ${item.timeEnd}"
        auditory.text = item.auditory
    }
}