package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
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
import com.maestrx.studentcontrol.teacherapp.spinner.TablesSpinnerAdapter
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.FilePicker
import com.maestrx.studentcontrol.teacherapp.util.capitalize
import com.maestrx.studentcontrol.teacherapp.util.toast
import com.maestrx.studentcontrol.teacherapp.viewmodel.StudentsImportUiState
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

    private var importBinding: DialogImportStudentsBinding? = null

    val viewModel by viewModels<StudentsViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<StudentsViewModelFactory> { factory ->
                factory.create(arguments?.getLong(Constants.GROUP_ID) ?: 0L)
            }
        }
    )

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

        val studentsAdapter = StudentsAdapter(
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
        binding.attended.adapter = studentsAdapter

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
                            group = Group(viewModel.groupId),
                        )
                    )
                    alertDialog.dismiss()
                } else {
                    toast(R.string.blank_name)
                }
            }
        }

        viewModel.studentsState.onEach { state ->
            studentsAdapter.submitList(state)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        var importDialog: AlertDialog? = null
        toolbarViewModel.importClicked
            .onEach { state ->
                if (state) {
                    importBinding = DialogImportStudentsBinding.inflate(inflater).apply {
                        chooseFile.setOnClickListener {
                            FilePicker(requireActivity().activityResultRegistry) { uri ->
                                if (uri != null) {
                                    viewModel.selectFile(uri)
                                }
                            }.apply {
                                pickExcelFile()
                            }
                        }
                    }
                    importDialog = AlertDialog.Builder(context)
                        .setView(importBinding?.root)
                        .setTitle(R.string.import_students)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            viewModel.cleanImportState()
                            toolbarViewModel.importClicked(false)
                        }
                        .show()

                    importDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                        try {
                            val importState = requireNotNull(createImportState())
                            if (viewModel.fileState.value == null) {
                                throw IllegalArgumentException(getString(R.string.choose_file_error))
                            }

                            if (importState.selectedTable.isBlank()) {
                                throw IllegalArgumentException(getString(R.string.sheet_name_error))
                            }
                            if (importState.column.isBlank()) {
                                throw IllegalArgumentException(getString(R.string.column_error))
                            }
                            if (importState.startX.toInt() < 1 || importState.endX.toInt() < 1
                                || importState.startX > importState.endX
                            ) {
                                throw IllegalArgumentException(getString(R.string.row_numbers_error))
                            }

                            importBinding?.apply {
                                progressBar.isVisible = true
                                fileContainer.isGone = true
                                this.tableNameContainer.isGone = true
                                this.column.isGone = true
                                this.startX.isGone = true
                                this.endX.isGone = true
                            }
                            importDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.isGone = true
                            importDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isGone = true

                            viewModel.saveImportState(importState)
                            viewModel.importStudents(viewModel.groupId)
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

        viewModel.fileState
            .onEach { uri ->
                if (uri != null) {
                    val documentFile =
                        DocumentFile.fromSingleUri(requireContext(), uri)
                    importBinding?.fileStr?.text = documentFile?.name ?: ""
                    importBinding?.fileStr?.isVisible = true
                } else {
                    importBinding?.fileStr?.isGone = true
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.importState
            .onEach { state ->
                importBinding?.apply {
                    tableNames.adapter = TablesSpinnerAdapter(requireContext(), state.tableNames)
                    if (state.tableNames.isNotEmpty() && !progressBar.isVisible) {
                        tableNameContainer.isVisible = true
                    } else {
                        tableNameContainer.isGone = true
                    }
                    column.setText(state.column)
                    startX.setText(state.startX)
                    endX.setText(state.endX)
                }
                state.tableNames.forEachIndexed { index, name ->
                    if (name == state.selectedTable) {
                        importBinding?.tableNames?.setSelection(index, false)
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

    private fun createImportState(): StudentsImportUiState? {
        importBinding?.apply {
            return StudentsImportUiState(
                fileName = fileStr.text.toString(),
                selectedTable = tableNames.selectedItem as String? ?: "",
                column = column.text.toString().trim(),
                startX = startX.text.toString().trim(),
                endX = endX.text.toString().trim(),
            )
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.saveImportState(createImportState())
        importBinding = null
    }
}