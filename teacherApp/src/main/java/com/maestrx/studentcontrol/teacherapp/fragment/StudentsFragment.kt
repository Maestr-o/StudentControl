package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogEditStudentBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogImportStudentsBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogMultilineTextBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentStudentsBinding
import com.maestrx.studentcontrol.teacherapp.excel.ExcelManager
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.recyclerview.students.StudentsAdapter
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.capitalize
import com.maestrx.studentcontrol.teacherapp.utils.toast
import com.maestrx.studentcontrol.teacherapp.viewmodel.StudentsViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.ToolbarViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.StudentsViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class StudentsFragment : Fragment() {

    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()

    @Inject
    lateinit var excelManager: ExcelManager

    override fun onStart() {
        super.onStart()
        toolbarViewModel.showImport(true)
    }

    override fun onStop() {
        super.onStop()
        toolbarViewModel.showImport(false)
    }

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
                override fun onEditClickListener(view: View, student: Student) {
                    view.isEnabled = false
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
                            val newFirstName =
                                dialogBinding.firstName.text.toString().trim().capitalize()
                            val newMidName =
                                dialogBinding.midName.text.toString().trim().capitalize()
                            val newLastName =
                                dialogBinding.lastName.text.toString().trim().capitalize()
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
                        .setOnDismissListener {
                            view.isEnabled = true
                        }
                        .show()
                }

                override fun onDeleteClickListener(view: View, student: Student) {
                    view.isEnabled = false
                    val dialogBinding = DialogMultilineTextBinding.inflate(inflater).apply {
                        line.text = getString(R.string.delete_student, student.fullName)
                    }
                    AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            viewModel.deleteById(student.id)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            view.isEnabled = true
                        }
                        .show()
                }
            }
        )
        binding.attended.adapter = adapter

        binding.add.setOnClickListener {
            val dialogBinding = DialogEditStudentBinding.inflate(inflater)
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.add_new_student))
                .setView(dialogBinding.root)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    val newFirstName = dialogBinding.firstName.text.toString().trim().capitalize()
                    val newMidName = dialogBinding.midName.text.toString().trim().capitalize()
                    val newLastName = dialogBinding.lastName.text.toString().trim().capitalize()
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

        toolbarViewModel.importClicked
            .onEach { state ->
                if (state) {
                    var filePath = ""
                    var sheetName = ""
                    var column = ""
                    var startX = 0
                    var endX = 0
                    val dialogBinding = DialogImportStudentsBinding.inflate(inflater).apply {
                        chooseFile.setOnClickListener {
                            // File chooser
                            if (filePath.isNotBlank()) {
                                fileStr.apply {
                                    text = filePath.split("/").last()
                                    isVisible = true
                                }
                            } else {
                                fileStr.isGone = true
                            }
                        }
                    }
                    AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .setTitle(R.string.import_students)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            // checks
                            // column letter to number
//                            excelManager.importStudents( - ViewModel
//                                path = filePath,
//                                sheetName = sheetName,
//                                groupId = groupId,
//                                column = 0,
//                                startX = startX,
//                                endX = endX
//                            )
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            toolbarViewModel.importClicked(false)
                        }
                        .show()
                }
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