package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogEditStudentBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentStudentsBinding
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.recyclerview.students.StudentsAdapter
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.toast
import com.maestrx.studentcontrol.teacherapp.viewmodel.StudentsViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.StudentsViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class StudentsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStudentsBinding.inflate(inflater, container, false)

        val groupId = arguments?.getLong(Constants.GROUP_ID) ?: 0L

        val viewModel by viewModels<StudentsViewModel>(
            extrasProducer = {
                defaultViewModelCreationExtras.withCreationCallback<StudentsViewModelFactory> { factory ->
                    factory.create(groupId)
                }
            }
        )

        val adapter = StudentsAdapter(
            object : StudentsAdapter.StudentsListener {
                override fun onEditClickListener(student: Student) {
                    val dialogBinding = DialogEditStudentBinding.inflate(inflater)
                    dialogBinding.apply {
                        firstName.setText(student.firstName)
                        midName.setText(student.midName)
                        lastName.setText(student.lastName)
                    }
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.edit_student_data))
                        .setView(dialogBinding.root)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            val newFirstName = dialogBinding.firstName.text.toString().trim()
                            val newMidName = dialogBinding.midName.text.toString().trim()
                            val newLastName = dialogBinding.lastName.text.toString().trim()
                            if (newFirstName.isNotBlank() && newLastName.isNotBlank()) {
                                viewModel.save(
                                    student.copy(
                                        firstName = newFirstName,
                                        midName = newMidName,
                                        lastName = newLastName,
                                    )
                                )
                            } else {
                                toast(R.string.blank_name)
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

                override fun onDeleteClickListener(student: Student) {
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.delete_student, student.fullName))
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            viewModel.deleteById(student.id)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        )
        binding.students.adapter = adapter

        binding.add.setOnClickListener {
            val dialogBinding = DialogEditStudentBinding.inflate(inflater)
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.add_new_student))
                .setView(dialogBinding.root)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    val newFirstName = dialogBinding.firstName.text.toString().trim()
                    val newMidName = dialogBinding.midName.text.toString().trim()
                    val newLastName = dialogBinding.lastName.text.toString().trim()
                    if (newFirstName.isNotBlank() && newLastName.isNotBlank()) {
                        viewModel.save(
                            Student(
                                firstName = newFirstName,
                                midName = newMidName,
                                lastName = newLastName,
                                group = Group(groupId),
                            )
                        )
                    } else {
                        toast(R.string.blank_name)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        viewModel.state.onEach { state ->
            adapter.submitList(state)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.message
            .onEach { event ->
                when (event.getContentIfNotHandled()) {
                    Constants.MESSAGE_ERROR_SAVING_STUDENT -> {
                        toast(R.string.error_saving_student)
                    }

                    Constants.MESSAGE_ERROR_DELETING_STUDENT -> {
                        toast(R.string.error_deleting_student)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}