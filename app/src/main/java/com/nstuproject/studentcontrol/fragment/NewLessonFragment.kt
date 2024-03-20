package com.nstuproject.studentcontrol.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentEditLessonBinding
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonType
import com.nstuproject.studentcontrol.model.Subject
import com.nstuproject.studentcontrol.spinner.subjects.SubjectsSpinnerAdapter
import com.nstuproject.studentcontrol.utils.TimeFormatter
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

        binding.date.setOnClickListener {
            showDatePicker()
        }

        binding.time.setOnClickListener {
            showTimePicker()
        }

        viewModel.subjectsState
            .onEach { subjects ->
                SubjectsSpinnerAdapter(requireContext(), subjects).apply {
                    binding.subjects.adapter = this
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        val lessonState = viewModel.lessonState.value
        binding.apply {
            if (lessonState.date.isBlank()) {
                date.setText(TimeFormatter.unixTimeToDateString(System.currentTimeMillis()))
            } else {
                date.setText(lessonState.date)
                time.setText(lessonState.time)
            }
            date.setText(TimeFormatter.unixTimeToDateString(System.currentTimeMillis()))
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
            // groups
            auditory.setText(lessonState.auditory)
            description.setText(lessonState.description)
        }

        toolbarViewModel.saveClicked.onEach { state ->
            if (state) {
                val lesson = formLesson()
                if (lesson.title.isBlank()) {
                    toast(R.string.enter_title)
                } else if (lesson.auditory.isBlank()) {
                    toast(R.string.enter_auditory)
//                } else if (lesson.groups.isEmpty()) {
//                    toast(R.string.enter_group)
                } else {
                    viewModel.updateLessonState(lesson)
                    viewModel.save()
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
            date = binding.date.text.toString(),
            time = binding.time.text.toString(),
            subject = subject,
            type = if (binding.typeLab.isChecked) {
                LessonType.LAB
            } else if (binding.typePractice.isChecked) {
                LessonType.PRACTICE
            } else {
                LessonType.LECTURE
            },
            auditory = binding.auditory.text.toString().trim(),
            description = binding.description.text.toString().trim(),
            // groups = выбранные элементы в RCView
        )
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
            val selectedDateInMillis = selectedCalendar.timeInMillis
            binding.date.setText(TimeFormatter.unixTimeToDateString(selectedDateInMillis))
        }, year, month, dayOfMonth)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, h, m ->
                val selectedTime = "$h:$m"
                binding.time.setText(selectedTime)
            },
            hourOfDay,
            minute,
            true
        )

        timePickerDialog.show()
    }
}