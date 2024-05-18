package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogEditStudentBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogImportStudentsBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogMultilineTextBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentStudentsBinding
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.recyclerview.students.StudentsAdapter
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.FilePicker
import com.maestrx.studentcontrol.teacherapp.util.capitalize
import com.maestrx.studentcontrol.teacherapp.util.toast
import com.maestrx.studentcontrol.teacherapp.viewmodel.StudentsViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.ToolbarViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.StudentsViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class StudentsFragment : Fragment() {

    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()

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
                override fun onClickListener(student: Student) {
                    if (viewModel.lessonsCount.value <= 0) {
                        toast(R.string.zero_lessons_error)
                    } else {
                        findNavController().navigate(
                            R.id.action_studentsFragment_to_reportFragment,
                            bundleOf(Constants.STUDENT_DATA to Json.encodeToString(student))
                        )
                    }
                }

                override fun onEditClickListener(view: View, student: Student) {
                    view.isEnabled = false
                    val dialogBinding = DialogEditStudentBinding.inflate(inflater)
                    dialogBinding.apply {
                        firstName.setText(student.firstName)
                        midName.setText(student.midName)
                        lastName.setText(student.lastName)
                    }
                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle(getString(R.string.edit_student_data))
                        .setView(dialogBinding.root)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), null)
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            view.isEnabled = true
                        }
                        .show()

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
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
                            alertDialog.dismiss()
                        } else {
                            toast(R.string.blank_name)
                        }
                    }
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
            val alertDialog = AlertDialog.Builder(context)
                .setTitle(getString(R.string.add_new_student))
                .setView(dialogBinding.root)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
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
                    alertDialog.dismiss()
                } else {
                    toast(R.string.blank_name)
                }
            }
        }

        viewModel.studentsState.onEach { state ->
            adapter.submitList(state)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        var importDialog: AlertDialog? = null
        toolbarViewModel.importClicked
            .onEach { state ->
                if (state) {
                    var fileUri: Uri? = null
                    var fileName = ""
                    val dialogBinding = DialogImportStudentsBinding.inflate(inflater).apply {
                        chooseFile.setOnClickListener {
                            FilePicker(requireActivity().activityResultRegistry) { uri ->
                                uri?.let {
                                    fileUri = it
                                    val documentFile =
                                        DocumentFile.fromSingleUri(requireContext(), it)
                                    fileName = documentFile?.name ?: ""
                                }
                                if (fileName.isNotBlank()) {
                                    fileStr.apply {
                                        text = getString(R.string.choose_file_path, fileName)
                                        isVisible = true
                                    }
                                } else {
                                    fileStr.isGone = true
                                }
                            }.apply {
                                pickExcelFile()
                            }
                        }
                    }
                    importDialog = AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .setTitle(R.string.import_students)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            toolbarViewModel.importClicked(false)
                        }
                        .show()

                    importDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                        try {
                            if (fileUri == null) {
                                throw IllegalArgumentException(getString(R.string.choose_file_error))
                            }
                            val sheetName = dialogBinding.sheetName.text.toString()
                            val column = dialogBinding.column.text.toString().trim()
                            val startX = dialogBinding.startX.text.toString().trim().toInt()
                            val endX = dialogBinding.endX.text.toString().trim().toInt()

                            if (sheetName.isBlank()) {
                                throw IllegalArgumentException(getString(R.string.sheet_name_error))
                            }
                            if (column.isBlank()) {
                                throw IllegalArgumentException(getString(R.string.column_error))
                            }
                            if (startX < 1 || endX < 1 || startX > endX) {
                                throw IllegalArgumentException(getString(R.string.row_numbers_error))
                            }

                            dialogBinding.apply {
                                progressBar.isVisible = true
                                fileContainer.isGone = true
                                this.sheetName.isGone = true
                                sheetContainer.isGone = true
                            }
                            importDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.isGone = true

                            viewModel.importStudents(
                                uri = fileUri,
                                sheetName = sheetName,
                                groupId = groupId,
                                column = column,
                                startX = startX,
                                endX = endX
                            )
                        } catch (e: NumberFormatException) {
                            toast(R.string.row_numbers_error)
                        } catch (e: IllegalArgumentException) {
                            toast(e.message ?: getString(R.string.check_input_data))
                        } catch (e: Exception) {
                            toast(R.string.import_error)
                        }
                    }
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

                    Constants.MESSAGE_OK_IMPORT -> {
                        toast(R.string.import_ok)
                        importDialog?.dismiss()
                    }

                    Constants.MESSAGE_ERROR_IMPORT -> {
                        toast(R.string.import_error)
                        importDialog?.dismiss()
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}