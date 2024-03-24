package com.nstuproject.studentcontrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentLessonDetailsBinding
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonType
import com.nstuproject.studentcontrol.recyclerview.groupsSelected.GroupSelectedAdapter
import com.nstuproject.studentcontrol.utils.Constants
import com.nstuproject.studentcontrol.viewmodel.LessonDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class LessonDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLessonDetailsBinding.inflate(inflater, container, false)

        val viewModel by viewModels<LessonDetailsViewModel>()

        arguments?.let {
            val lesson = Json.decodeFromString<Lesson>(
                it.getString(Constants.LESSON_DATA) ?: Lesson().toString()
            )
            viewModel.setLesson(lesson)
        }

        val groupsAdapter = GroupSelectedAdapter()
        binding.groups.adapter = groupsAdapter

        viewModel.lessonState
            .onEach { state ->
                groupsAdapter.submitList(state.groups)
                binding.apply {
                    subject.text = state.subject.name
                    datetime.text = getString(
                        R.string.lesson_details_datetime,
                        state.date,
                        state.timeStart,
                        state.timeEnd
                    )
                    auditory.text = state.auditory
                    type.text = when (state.type.name) {
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
                    title.text = state.title
                    if (state.description.isNotBlank()) {
                        notesLayout.isVisible = true
                        notes.text = state.description
                    } else {
                        notesLayout.isGone = true
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}