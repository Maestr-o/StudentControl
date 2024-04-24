package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogDataControlBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogMultilineTextBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentLessonsBinding
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.recyclerview.lessons.LessonAdapter
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.TimeFormatter
import com.maestrx.studentcontrol.teacherapp.utils.toast
import com.maestrx.studentcontrol.teacherapp.viewmodel.LessonsViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class LessonsFragment : Fragment() {

    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()
    private val viewModel by viewModels<LessonsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLessonsBinding.inflate(layoutInflater)

        val adapter = LessonAdapter(
            object : LessonAdapter.LessonsListener {
                override fun onClickListener(lesson: Lesson) {
                    val data = Json.encodeToString(lesson)
                    val bundle = Bundle().apply {
                        putString(Constants.LESSON_DATA, data)
                    }
                    requireParentFragment().requireParentFragment().findNavController()
                        .navigate(
                            R.id.action_bottomNavigationFragment_to_lessonDetailsFragment,
                            bundle
                        )
                }
            }
        )
        binding.lessons.adapter = adapter

        binding.datePrev.setOnClickListener {
            viewModel.decDate()
        }

        binding.dateNext.setOnClickListener {
            viewModel.incDate()
        }

        binding.dateSelect.setOnClickListener {
            showDatePicker()
        }

        viewModel.date
            .onEach { date ->
                binding.dateSelect.text = TimeFormatter.unixTimeToDateStringWithDayOfWeek(date)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.state
            .onEach { state ->
                adapter.submitList(state)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        toolbarViewModel.dataControlClicked
            .onEach { state ->
                if (state) {
                    val dialogBinding = DialogDataControlBinding.inflate(inflater).apply {
                        clean.setOnClickListener {
                            val sureDialogBinding =
                                DialogMultilineTextBinding.inflate(inflater).apply {
                                    line.text = getString(R.string.sure_clean_data)
                                }
                            AlertDialog.Builder(context)
                                .setView(sureDialogBinding.root)
                                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                    viewModel.cleanDatabase()
                                    dialog.dismiss()
                                }
                                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }

                        exportExcel.setOnClickListener {

                        }
                    }
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.data_control))
                        .setView(dialogBinding.root)
                        .setNegativeButton(getString(R.string.back)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    toolbarViewModel.dataControlClicked(false)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        requireActivity().supportFragmentManager.setFragmentResultListener(
            Constants.LESSON_UPDATED,
            viewLifecycleOwner
        ) { _, _ ->
            val startTime = viewModel.date.value
            viewModel.updateLessonsForPeriod(startTime, TimeFormatter.getEndTime(startTime))
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })

        return binding.root
    }

    fun addLesson() {
        if (viewModel.groupsCount.value == 0L) {
            toast(R.string.zero_groups_error)
        } else if (viewModel.subjectsCount.value == 0L) {
            toast(R.string.zero_subjects_error)
        } else {
            val bundle = Bundle().apply {
                putLong(Constants.NEW_LESSON_DATE, viewModel.date.value)
            }
            requireParentFragment().requireParentFragment().findNavController()
                .navigate(R.id.action_bottomNavigationFragment_to_newLessonFragment, bundle)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, y, m, dOfM ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, y)
                set(Calendar.MONTH, m)
                set(Calendar.DAY_OF_MONTH, dOfM)
            }
            val selectedDateInMillis = TimeFormatter.getDateZeroTime(selectedCalendar.timeInMillis)
            viewModel.setDate(selectedDateInMillis)
        }, year, month, dayOfMonth)

        datePickerDialog.show()
    }
}