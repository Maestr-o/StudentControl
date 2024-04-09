package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
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
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentEditLessonBinding
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.LessonType
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.recyclerview.groupsSelect.GroupSelectAdapter
import com.maestrx.studentcontrol.teacherapp.spinner.subjects.SubjectsSpinnerAdapter
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.TimeFormatter
import com.maestrx.studentcontrol.teacherapp.utils.showDatePicker
import com.maestrx.studentcontrol.teacherapp.utils.showTimeEndPicker
import com.maestrx.studentcontrol.teacherapp.utils.showTimeStartPicker
import com.maestrx.studentcontrol.teacherapp.utils.toast
import com.maestrx.studentcontrol.teacherapp.viewmodel.EditLessonViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class EditLessonFragment : Fragment() {

    private var _binding: FragmentEditLessonBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<EditLessonViewModel>()
    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()

    private lateinit var groupsAdapter: GroupSelectAdapter

    private val selDate =
        arguments?.getLong(Constants.NEW_LESSON_DATE) ?: TimeFormatter.getCurrentDateZeroTime()

    override fun onStart() {
        super.onStart()
        toolbarViewModel.showSave(true)
        toolbarViewModel.showDelete(true)
    }

    override fun onStop() {
        super.onStop()
        toolbarViewModel.showSave(false)
        toolbarViewModel.showDelete(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditLessonBinding.inflate(inflater, container, false)

        arguments?.let {
            viewModel.updateLessonState(
                Json.decodeFromString(
                    it.getString(
                        Constants.LESSON_DATA,
                        Lesson().toString()
                    )
                )
            )
        }

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
                subjects.forEachIndexed { index, subject ->
                    if (subject == viewModel.lessonState.value.subject) {
                        binding.subjects.setSelection(index, false)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        val lesson = viewModel.lessonState.value
        binding.apply {
            if (lesson.timeStart == 0L) {
                date.setText(
                    TimeFormatter.unixTimeToDateString(selDate)
                )
            } else {
                date.setText(TimeFormatter.unixTimeToDateString(lesson.timeStart))
                timeStart.setText(TimeFormatter.unixTimeToTimeString(lesson.timeStart))
                timeEnd.setText(TimeFormatter.unixTimeToTimeString(lesson.timeEnd))
            }
            title.setText(lesson.title)
            when (lesson.type) {
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
            auditory.setText(lesson.auditory)
            notes.setText(lesson.description)
        }

        toolbarViewModel.saveClicked.onEach { state ->
            if (state) {
                val newLesson = formLesson()
                if (newLesson.title.isBlank()) {
                    toast(R.string.enter_title)
                } else if (newLesson.auditory.isBlank()) {
                    toast(R.string.enter_auditory)
                } else if (viewModel.selectedGroupsState.value.selectedPositions.isEmpty()) {
                    toast(R.string.enter_group)
                } else {
                    viewModel.updateLessonState(newLesson)
                    viewModel.save()
                    val bundle = Bundle().apply {
                        putString(
                            Constants.LESSON_DATA,
                            Json.encodeToString(viewModel.lessonState.value)
                        )
                    }
                    requireActivity().supportFragmentManager.setFragmentResult(
                        Constants.LESSON_UPDATED,
                        bundleOf()
                    )
                    findNavController().navigate(
                        R.id.action_editLessonFragment_to_lessonDetailsFragment,
                        bundle
                    )
                }
            }
            toolbarViewModel.saveClicked(false)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        toolbarViewModel.deleteClicked
            .onEach { state ->
                if (state) {
                    AlertDialog.Builder(context)
                        .setTitle(R.string.ask_delete_lesson)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            viewModel.deleteLesson()
                            requireActivity().supportFragmentManager.setFragmentResult(
                                Constants.LESSON_UPDATED,
                                bundleOf()
                            )
                            findNavController().navigate(R.id.action_editLessonFragment_to_bottomNavigationFragment)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    toolbarViewModel.deleteClicked(false)
                }
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
        viewModel.updateGroups(viewModel.selectedGroupsState.value.selectedGroups)
        return Lesson(
            id = viewModel.lessonState.value.id,
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
        )
    }
}