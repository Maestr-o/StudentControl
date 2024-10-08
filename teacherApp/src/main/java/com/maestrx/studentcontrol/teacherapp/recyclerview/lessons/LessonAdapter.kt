package com.maestrx.studentcontrol.teacherapp.recyclerview.lessons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.maestrx.studentcontrol.teacherapp.databinding.CardLessonBinding
import com.maestrx.studentcontrol.teacherapp.model.Lesson

class LessonAdapter(
    private val listener: LessonsListener,
) : ListAdapter<Lesson, LessonsViewHolder>(LessonsDiffCallback()) {

    interface LessonsListener {
        fun onClickListener(lesson: Lesson)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardLessonBinding.inflate(inflater, parent, false)
        val viewHolder = LessonsViewHolder(binding)

        binding.root.setOnClickListener {
            listener.onClickListener(getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: LessonsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}