package com.nstuproject.studentcontrol.recyclerview.lessons

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nstuproject.studentcontrol.databinding.CardLessonBinding
import com.nstuproject.studentcontrol.model.Lesson

class LessonsViewHolder(
    private val binding: CardLessonBinding
) : ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: Lesson) = with(binding) {
        subject.text = item.subject.name
        type.text = item.type.name
        title.text = item.title
        time.text = "${item.date} - ${item.time}"
        auditory.text = item.auditory
    }
}