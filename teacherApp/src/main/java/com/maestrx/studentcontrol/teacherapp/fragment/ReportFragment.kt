package com.maestrx.studentcontrol.teacherapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentReportBinding
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.spinner.subjects.SubjectsSpinnerAdapter
import com.maestrx.studentcontrol.teacherapp.utils.Constants
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

        viewModel.subjectState
            .onEach { subjects ->
                SubjectsSpinnerAdapter(requireContext(), subjects).apply {
                    binding.subjects.adapter = this
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.reportState
            .onEach { state ->
                binding.apply {
                    group.text = getString(R.string.student_group, state.student.group)
                    name.text = getString(R.string.student_name, state.student.fullName)
                    percentage.setPercentage(state.percentage)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}