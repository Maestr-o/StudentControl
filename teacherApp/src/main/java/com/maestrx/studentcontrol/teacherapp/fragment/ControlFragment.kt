package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogMarkManuallyBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogMultilineTextBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentControlBinding
import com.maestrx.studentcontrol.teacherapp.model.ControlStatus
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.LessonType
import com.maestrx.studentcontrol.teacherapp.recyclerview.attended_students.AttendedStudentsAdapter
import com.maestrx.studentcontrol.teacherapp.recyclerview.groups_selected.GroupSelectedAdapter
import com.maestrx.studentcontrol.teacherapp.recyclerview.manual_mark.ManualMarkAdapter
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.TimeFormatter
import com.maestrx.studentcontrol.teacherapp.utils.toast
import com.maestrx.studentcontrol.teacherapp.viewmodel.ControlViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.ToolbarViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.ControlViewModelFactory
import com.maestrx.studentcontrol.teacherapp.wifi.WifiHelper
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class ControlFragment : Fragment() {

    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()
    private lateinit var _viewModel: Lazy<ControlViewModel>
    private val viewModel get() = _viewModel.value

    private var _binding: FragmentControlBinding? = null
    private val binding get() = _binding!!

    private var loopJob: Job? = null

    private lateinit var markAdapter: ManualMarkAdapter

    private var lastStudentsList: List<Any> = mutableListOf()

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
        _binding = FragmentControlBinding.inflate(inflater, container, false)

        val lesson = arguments?.let {
            Json.decodeFromString<Lesson>(
                it.getString(Constants.LESSON_DATA)!!
            )
        } ?: Lesson()

        _viewModel = viewModels<ControlViewModel>(
            extrasProducer = {
                defaultViewModelCreationExtras.withCreationCallback<ControlViewModelFactory> { factory ->
                    factory.create(lesson)
                }
            }
        )

        val groupsAdapter = GroupSelectedAdapter()
        binding.groups.adapter = groupsAdapter

        groupsAdapter.submitList(lesson.groups)
        binding.apply {
            subject.text = lesson.subject.name
            datetime.text = getString(
                R.string.control_datetime,
                TimeFormatter.unixTimeToDateString(lesson.timeStart),
                TimeFormatter.unixTimeToTimeString(lesson.timeStart),
                TimeFormatter.unixTimeToTimeString(lesson.timeEnd)
            )
            auditory.text = lesson.auditory
            type.text = when (lesson.type.name) {
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
            title.text = lesson.title
            if (lesson.description.isNotBlank()) {
                notesLayout.isVisible = true
                notes.text = lesson.description
            } else {
                notesLayout.isGone = true
            }
        }

        binding.startControl.setOnClickListener {
            if (viewModel.controlStatus.value is ControlStatus.ReadyToStart) {
                val dialogBinding = DialogMultilineTextBinding.inflate(inflater).apply {
                    line.text = getString(R.string.create_ap_hint)
                }
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.create_ap))
                    .setView(dialogBinding.root)
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        WifiHelper.goToAPSettings(requireContext())
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else if (viewModel.controlStatus.value is ControlStatus.Running) {
                WifiHelper.goToAPSettings(requireContext())
            }
        }

        if (viewModel.isManualMarkDialogShowed.value) {
            manualMarkDialog(inflater)
        }

        binding.markManually.setOnClickListener {
            binding.markManually.isEnabled = false
            viewModel.updateNotMarkedStudentsWithGroups()
        }

        toolbarViewModel.editClicked
            .onEach {
                if (toolbarViewModel.editClicked.value) {
                    val bundle = Bundle().apply {
                        putString(
                            Constants.LESSON_DATA,
                            Json.encodeToString(viewModel.lesson)
                        )
                    }
                    findNavController().navigate(
                        R.id.action_controlFragment_to_editLessonFragment,
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
                            findNavController().navigateUp()
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            toolbarViewModel.deleteClicked(false)
                        }
                        .show()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        loopJob = lifecycleScope.launch {
            while (true) {
                delay(Constants.TIME_CHECK_DELAY)
                checkControlStatus()
            }
        }

        viewModel.controlStatus
            .onEach { state ->
                when (state) {
                    is ControlStatus.Loading -> {
                        binding.apply {
                            progressBar.isVisible = true
                            mainContainer.isGone = true
                        }
                    }

                    is ControlStatus.NotReadyToStart -> {
                        toolbarViewModel.setControlRunning(false)
                        binding.apply {
                            progressBar.isGone = true
                            mainContainer.isVisible = true
                            startControl.isEnabled = false
                            startControl.text = getString(R.string.early_control)
                            markManually.isEnabled = false
                        }
                    }

                    ControlStatus.ReadyToStart -> {
                        toolbarViewModel.setControlRunning(false)
                        binding.apply {
                            progressBar.isGone = true
                            mainContainer.isVisible = true
                            startControl.isEnabled = true
                            startControl.text = getString(R.string.start_control)
                            markManually.isEnabled = true
                        }
                    }

                    ControlStatus.Running -> {
                        toolbarViewModel.setControlRunning(true)
                        binding.apply {
                            progressBar.isGone = true
                            mainContainer.isVisible = true
                            startControl.isEnabled = true
                            startControl.text = getString(R.string.stop_control)
                            markManually.isEnabled = true
                        }
                    }

                    ControlStatus.Finished -> {
                        toolbarViewModel.setControlRunning(false)
                        binding.apply {
                            progressBar.isGone = true
                            mainContainer.isVisible = true
                            startControl.isEnabled = false
                            startControl.text = getString(R.string.end_control)
                            markManually.isEnabled = true
                        }
                    }

                    ControlStatus.Full -> {
                        toolbarViewModel.setControlRunning(false)
                        binding.apply {
                            progressBar.isGone = true
                            mainContainer.isVisible = true
                            startControl.isEnabled = false
                            startControl.text = getString(R.string.end_control)
                            markManually.isEnabled = false
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.message
            .onEach { event ->
                event.getContentIfNotHandled()?.let { message ->
                    when (message) {
                        Constants.MESSAGE_ERROR_DELETING_LESSON -> {
                            toast(R.string.error_deleting_lesson)
                        }

                        Constants.MESSAGE_ERROR_SAVING_MARKS -> {
                            toast(R.string.error_saving_marks)
                        }

                        Constants.MESSAGE_SHOW_MARK_DIALOG -> {
                            manualMarkDialog(inflater)
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.controlStatus.value is ControlStatus.Running) {
                        val dialogBinding = DialogMultilineTextBinding.inflate(inflater)
                        dialogBinding.line.text = getString(R.string.quit_from_control)
                        AlertDialog.Builder(context)
                            .setView(dialogBinding.root)
                            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                findNavController().navigateUp()
                                dialog.dismiss()
                            }
                            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    } else {
                        findNavController().navigateUp()
                    }
                }
            })

        return binding.root
    }

    private fun checkControlStatus() {
        val status = viewModel.controlStatus.value
        updateAttendedStudents()
        if (status is ControlStatus.ReadyToStart || status is ControlStatus.NotReadyToStart
            || status is ControlStatus.Running || status is ControlStatus.Loading
        ) {
            val lesson = viewModel.lesson
            val startTime = TimeFormatter.decRecess(lesson.timeStart)
            val endTime = lesson.timeEnd
            val time = System.currentTimeMillis()

            val studentsState = viewModel.studentsWithGroupsState.value
            if (studentsState.totalStudentsCount == studentsState.marks.count()
                && studentsState.totalStudentsCount > 0 && status !is ControlStatus.Full
            ) {
                viewModel.setControlStatus(ControlStatus.Full)
            } else if (time in startTime..endTime && WifiHelper.isWifiApEnabled(requireContext()) &&
                status !is ControlStatus.Running
            ) {
                viewModel.setControlStatus(ControlStatus.Running)
            } else if (time in startTime..endTime && !WifiHelper.isWifiApEnabled(requireContext())
                && status !is ControlStatus.ReadyToStart
            ) {
                viewModel.setControlStatus(ControlStatus.ReadyToStart)
            } else if (time < startTime && status !is ControlStatus.NotReadyToStart) {
                viewModel.setControlStatus(ControlStatus.NotReadyToStart)
            } else if (time > endTime && status !is ControlStatus.Finished) {
                viewModel.setControlStatus(ControlStatus.Finished)
            }
        }
    }

    private fun manualMarkDialog(inflater: LayoutInflater) {
        val dialogBinding = DialogMarkManuallyBinding.inflate(inflater).apply {
            val items = viewModel.getMarkList()
            if (items.isEmpty()) {
                toast(R.string.no_unmarked_students)
                binding.markManually.isEnabled = true
                return
            }
            markAdapter = ManualMarkAdapter(items)
            students.adapter = markAdapter
        }

        viewModel.setMarkDialogShow(true)

        AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                viewModel.addMarks(markAdapter.items)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                binding.markManually.isEnabled = true
                viewModel.setMarkDialogShow(false)
            }
            .show()
    }

    private fun updateAttendedStudents() {
        val state = viewModel.studentsWithGroupsState.value
        binding.registeredCount.text =
            getString(
                R.string.registered_students,
                state.marks.count(),
                state.totalStudentsCount
            )
        if (state.markedStudentsWithGroups != lastStudentsList) {
            binding.attended.adapter = AttendedStudentsAdapter(state.markedStudentsWithGroups)
            lastStudentsList = state.markedStudentsWithGroups
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (viewModel.isManualMarkDialogShowed.value) {
            viewModel.setNotMarkedStudentsWithGroups(markAdapter.items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loopJob?.cancel()
        lastStudentsList = mutableListOf()
        _binding = null
    }
}