package com.maestrx.studentcontrol.teacherapp.fragment

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
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentLessonsBinding
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.recyclerview.lessons.LessonAdapter
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.TimeFormatter
import com.maestrx.studentcontrol.teacherapp.utils.toast
import com.maestrx.studentcontrol.teacherapp.viewmodel.LessonsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
                    val data = Json.encodeToString(lesson)
                    val bundle = Bundle().apply {
                        putString(Constants.LESSON_DATA, data)
                    }
                    requireParentFragment().requireParentFragment().findNavController()
                        .navigate(
                            R.id.action_bottomNavigationFragment_to_controlFragment,
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

        binding.dateSelect.setOnClickListener { view ->
            showDatePicker(view)
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

        viewModel.lessonsCount
            .onEach { _ ->
                updateList()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        requireActivity().supportFragmentManager.setFragmentResultListener(
            Constants.LESSON_UPDATED,
            viewLifecycleOwner
        ) { _, _ ->
            updateList()
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

    private fun showDatePicker(view: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, y, m, dOfM ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, y)
                set(Calendar.MONTH, m)
                set(Calendar.DAY_OF_MONTH, dOfM)
            }
            val selectedDateInMillis = TimeFormatter.getDateZeroTime(selectedCalendar.timeInMillis)
            viewModel.setDate(selectedDateInMillis)
            view.isEnabled = true
        }, year, month, dayOfMonth).apply {
            setOnDismissListener {
                view.isEnabled = true
            }
            view.isEnabled = false
            show()
        }
    }

    private fun updateList() {
        val startTime = viewModel.date.value
        viewModel.updateLessonsForPeriod(startTime, TimeFormatter.getEndTime(startTime))
    }
}