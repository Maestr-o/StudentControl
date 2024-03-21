package com.nstuproject.studentcontrol.fragment

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentLessonsBinding
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.recyclerview.lessons.LessonAdapter
import com.nstuproject.studentcontrol.utils.Constants
import com.nstuproject.studentcontrol.utils.TimeFormatter
import com.nstuproject.studentcontrol.utils.toast
import com.nstuproject.studentcontrol.viewmodel.LessonsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LessonsFragment : Fragment() {

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
                    // send lesson
                    requireParentFragment().requireParentFragment().findNavController()
                        .navigate(R.id.action_bottomNavigationFragment_to_lessonDetailsFragment)
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