package com.nstuproject.studentcontrol.fragment

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.DialogMultilineTextBinding
import com.nstuproject.studentcontrol.databinding.FragmentLessonDetailsBinding
import com.nstuproject.studentcontrol.model.ControlStatus
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonType
import com.nstuproject.studentcontrol.recyclerview.groupsSelected.GroupSelectedAdapter
import com.nstuproject.studentcontrol.utils.Constants
import com.nstuproject.studentcontrol.utils.TimeFormatter
import com.nstuproject.studentcontrol.viewmodel.LessonDetailsViewModel
import com.nstuproject.studentcontrol.viewmodel.ToolbarViewModel
import com.nstuproject.studentcontrol.viewmodel.di.LessonDetailsViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class LessonDetailsFragment : Fragment() {

    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()
    private lateinit var _viewModel: Lazy<LessonDetailsViewModel>
    private val viewModel get() = _viewModel.value

    private lateinit var lessonArg: Lesson

    override fun onStart() {
        super.onStart()
        toolbarViewModel.showDelete(true)
        toolbarViewModel.showEdit(true)
    }

    override fun onStop() {
        super.onStop()
        toolbarViewModel.showDelete(false)
        toolbarViewModel.showEdit(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLessonDetailsBinding.inflate(inflater, container, false)

        lessonArg = arguments?.let {
            Json.decodeFromString<Lesson>(
                it.getString(Constants.LESSON_DATA) ?: Lesson().toString()
            )
        } ?: Lesson()

        _viewModel = viewModels<LessonDetailsViewModel>(
            extrasProducer = {
                defaultViewModelCreationExtras.withCreationCallback<LessonDetailsViewModelFactory> { factory ->
                    factory.create(lessonArg)
                }
            }
        )

        val groupsAdapter = GroupSelectedAdapter()
        binding.groups.adapter = groupsAdapter

        binding.startControl.setOnClickListener {
            val dialogBinding = DialogMultilineTextBinding.inflate(inflater)
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.create_ap))
                .setView(dialogBinding.root)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    val intent = Intent().apply {
                        component = ComponentName(
                            "com.android.settings",
                            "com.android.settings.Settings\$TetherSettingsActivity"
                        )
                    }
                    startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        toolbarViewModel.editClicked
            .onEach {
                if (toolbarViewModel.editClicked.value) {
                    val bundle = Bundle().apply {
                        putString(
                            Constants.LESSON_DATA,
                            Json.encodeToString(viewModel.lessonState.value)
                        )
                    }
                    findNavController().navigate(
                        R.id.action_lessonDetailsFragment_to_editLessonFragment,
                        bundle
                    )
                    toolbarViewModel.editClicked(false)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        toolbarViewModel.deleteClicked
            .onEach {
                if (toolbarViewModel.deleteClicked.value) {
                    AlertDialog.Builder(context)
                        .setTitle(R.string.ask_delete_lesson)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            viewModel.deleteLesson()
                            requireActivity().supportFragmentManager.setFragmentResult(
                                Constants.LESSON_UPDATED,
                                bundleOf()
                            )
                            findNavController().navigateUp()
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

        viewModel.lessonState
            .onEach { state ->
                groupsAdapter.submitList(state.groups)
                binding.apply {
                    subject.text = state.subject.name
                    datetime.text = getString(
                        R.string.lesson_details_datetime,
                        TimeFormatter.unixTimeToDateString(state.timeStart),
                        TimeFormatter.unixTimeToTimeString(state.timeStart),
                        TimeFormatter.unixTimeToTimeString(state.timeEnd)
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

        lifecycleScope.launch {
            while (true) {
                checkTime()
                delay(Constants.TIME_CHECK_DELAY)
            }
        }

        viewModel.controlStatus
            .onEach { state ->
                when (state) {
                    is ControlStatus.NotReadyToStart -> {
                        toolbarViewModel.startControl(false)
                        binding.apply {
                            startControl.isEnabled = false
                            startControl.text = getString(R.string.early_control)
                            registeredCount.isGone = true
                            students.isGone = true
                        }
                    }

                    ControlStatus.ReadyToStart -> {
                        toolbarViewModel.startControl(false)
                        binding.apply {
                            startControl.isEnabled = true
                            startControl.text = getString(R.string.start_control)
                            registeredCount.isGone = true
                            students.isGone = true
                        }
                    }

                    ControlStatus.Running -> {
                        toolbarViewModel.startControl(true)
                        binding.apply {
                            startControl.isEnabled = true
                            startControl.text = getString(R.string.stop_control)
                            registeredCount.isVisible = true
                            students.isVisible = true
                        }
                    }

                    ControlStatus.Finished -> {
                        toolbarViewModel.startControl(false)
                        binding.apply {
                            startControl.isEnabled = false
                            startControl.text = getString(R.string.end_control)
                            registeredCount.isVisible = true
                            students.isVisible = true
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (viewModel.controlStatus.value is ControlStatus.Running) {
                showExitDialog()
            } else {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

    private fun checkTime() {
        val status = viewModel.controlStatus.value
        if (status is ControlStatus.ReadyToStart || status is ControlStatus.NotReadyToStart) {
            val lessonVM = viewModel.lessonState.value
            val lesson = if (lessonVM.timeStart == 0L) {
                lessonArg
            } else {
                lessonVM
            }
            val startTime = TimeFormatter.decRecess(lesson.timeStart)
            val endTime = lesson.timeEnd
            val time = System.currentTimeMillis()

            if (time in startTime..endTime) {
                viewModel.setControlStatus(ControlStatus.ReadyToStart)
            } else if (time < startTime) {
                viewModel.setControlStatus(ControlStatus.NotReadyToStart)
            } else if (time > endTime) {
                viewModel.setControlStatus(ControlStatus.Finished)
            }
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.ask_stop_control))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}