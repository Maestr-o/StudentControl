package com.nstuproject.studentcontrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentEditLessonBinding
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonType
import com.nstuproject.studentcontrol.model.Subject
import com.nstuproject.studentcontrol.recyclerview.groupsSelect.GroupSelectAdapter
import com.nstuproject.studentcontrol.spinner.subjects.SubjectsSpinnerAdapter
import com.nstuproject.studentcontrol.utils.Constants
import com.nstuproject.studentcontrol.utils.TimeFormatter
import com.nstuproject.studentcontrol.utils.showDatePicker
import com.nstuproject.studentcontrol.utils.showTimeEndPicker
import com.nstuproject.studentcontrol.utils.showTimeStartPicker
import com.nstuproject.studentcontrol.utils.toast
import com.nstuproject.studentcontrol.viewmodel.NewLessonViewModel
import com.nstuproject.studentcontrol.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class NewLessonFragment : Fragment() {

    private var _binding: FragmentEditLessonBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<NewLessonViewModel>()
    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()

    private lateinit var groupsAdapter: GroupSelectAdapter

    private val selDate =
        arguments?.getLong(Constants.NEW_LESSON_DATE) ?: TimeFormatter.getCurrentDateZeroTime()

    override fun onStart() {
        super.onStart()
        toolbarViewModel.showSave(true)
    }

    override fun onStop() {
        super.onStop()
        toolbarViewModel.showSave(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditLessonBinding.inflate(inflater, container, false)

        groupsAdapter = GroupSelectAdapter(
            object : GroupSelectAdapter.GroupChooseListener {
                override fun groupChooseClick(item: Group, position: Int) {
                    val positions = viewModel.selectedGroupsState.value.selectedPositions
                    if (positions.contains(position)) {
                        groupsAdapter.removeItem(position)
                        viewModel.removeGroup(item, position)
                    } else {
                        groupsAdapter.addItem(position)
                        viewModel.addGroup(item, position)
                    }
                }
            }
        )
        binding.groups.adapter = groupsAdapter

        binding.date.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDatePicker(binding.date)
                v.clearFocus()
            }
        }

        binding.timeStart.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showTimeStartPicker(binding.timeStart, binding.timeEnd)
                v.clearFocus()
            }
        }

        binding.timeEnd.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showTimeEndPicker(binding.timeEnd, binding.timeStart.text.toString())
                v.clearFocus()
            }
        }

        viewModel.groupsState
            .onEach { state ->
                groupsAdapter.submitList(state)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.selectedGroupsState
            .onEach { state ->
                groupsAdapter.setItems(state.selectedPositions)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.subjectsState
            .onEach { subjects ->
                SubjectsSpinnerAdapter(requireContext(), subjects).apply {
                    binding.subjects.adapter = this
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        val lessonState = viewModel.lessonState.value
        binding.apply {
            if (lessonState.timeStart == 0L) {
                date.setText(
                    TimeFormatter.unixTimeToDateString(selDate)
                )
            } else {
                date.setText(TimeFormatter.unixTimeToDateString(lessonState.timeStart))
                timeStart.setText(TimeFormatter.unixTimeToTimeString(lessonState.timeStart))
                timeEnd.setText(TimeFormatter.unixTimeToTimeString(lessonState.timeEnd))
            }
            title.setText(lessonState.title)
            when (lessonState.type) {
                LessonType.LECTURE -> {
                    typeLecture.isChecked = true
                }

                LessonType.LAB -> {
                    typeLab.isChecked = true
                }

                LessonType.PRACTICE -> {
                    typePractice.isChecked = true
                }
            }
            auditory.setText(lessonState.auditory)
            notes.setText(lessonState.description)
        }

        toolbarViewModel.saveClicked.onEach { state ->
            if (state) {
                val lesson = formLesson()
                if (lesson.title.isBlank()) {
                    toast(R.string.enter_title)
                } else if (lesson.auditory.isBlank()) {
                    toast(R.string.enter_auditory)
                } else if (lesson.groups.isEmpty()) {
                    toast(R.string.enter_group)
                } else {
                    viewModel.updateLessonState(lesson)
                    viewModel.save()
                    requireActivity().supportFragmentManager.setFragmentResult(
                        Constants.LESSON_UPDATED,
                        bundleOf()
                    )
                    findNavController().navigateUp()
                }
            }
            toolbarViewModel.saveClicked(false)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateLessonState(formLesson())
        _binding = null
    }

    private fun formLesson(): Lesson {
        val subject = (binding.subjects.selectedItem as Subject)
        return Lesson(
            title = binding.title.text.toString().trim(),
            timeStart = TimeFormatter.stringToUnixTime(
                binding.date.text.toString(),
                binding.timeStart.text.toString()
            ),
            timeEnd = TimeFormatter.stringToUnixTime(
                binding.date.text.toString(),
                binding.timeEnd.text.toString()
            ),
            subject = subject,
            type = if (binding.typeLab.isChecked) {
                LessonType.LAB
            } else if (binding.typePractice.isChecked) {
                LessonType.PRACTICE
            } else {
                LessonType.LECTURE
            },
            auditory = binding.auditory.text.toString().trim(),
            description = binding.notes.text.toString().trim(),
            groups = viewModel.selectedGroupsState.value.selectedGroups
        )
    }
}