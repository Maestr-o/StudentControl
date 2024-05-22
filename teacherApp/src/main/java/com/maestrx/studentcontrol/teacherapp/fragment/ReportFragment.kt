package com.maestrx.studentcontrol.teacherapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentReportBinding
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.recyclerview.report_lessons.ReportLessonsAdapter
import com.maestrx.studentcontrol.teacherapp.spinner.SubjectsSpinnerAdapter
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.toast
import com.maestrx.studentcontrol.teacherapp.viewmodel.ReportViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.ReportViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class ReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentReportBinding.inflate(inflater)

        val data = arguments?.getString(Constants.STUDENT_DATA) ?: Json.encodeToString(Student())
        val viewModel by viewModels<ReportViewModel>(
            extrasProducer = {
                defaultViewModelCreationExtras.withCreationCallback<ReportViewModelFactory> { factory ->
                    factory.create(Json.decodeFromString(data))
                }
            }
        )

        val reportLessonsAdapter = ReportLessonsAdapter()
        binding.lessons.adapter = reportLessonsAdapter

        binding.saveMarks.setOnClickListener {
            viewModel.changeMarks(reportLessonsAdapter.items)
        }

        viewModel.subjectState
            .onEach { subjects ->
                if (subjects.isEmpty()) return@onEach
                SubjectsSpinnerAdapter(requireContext(), subjects).apply {
                    binding.subjects.adapter = this
                    binding.subjects.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                viewModel.formReport(subjects[position])
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }
                subjects.forEachIndexed { index, subject ->
                    if (subject == viewModel.reportState.value.subject) {
                        binding.subjects.setSelection(index, false)
                        viewModel.formReport(subject)
                        return@onEach
                    }
                }
                binding.subjects.setSelection(0, false)
                viewModel.formReport(subjects[0])
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.reportState
            .onEach { state ->
                binding.apply {
                    group.text = getString(R.string.student_group, state.student.group)
                    name.text = getString(R.string.student_name, state.student.fullName)
                    percentage.setPercentage(state.percentage)
                    reportLessonsAdapter.updateList(state.reportLessons)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.message
            .onEach { event ->
                event.getContentIfNotHandled()?.let { message ->
                    when (message) {
                        Constants.MESSAGE_ERROR_MANUAL_SAVING_MARKS -> {
                            toast(R.string.error_saving_marks)
                        }

                        Constants.MESSAGE_OK_MANUAL_SAVING_MARKS -> {
                            toast(R.string.ok_saving_marks)
                        }
                    }
                    viewModel.formReport(viewModel.reportState.value.subject)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}