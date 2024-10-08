package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogEditLineBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogMultilineTextBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentSubjectsBinding
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.recyclerview.subjects.SubjectsAdapter
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.capitalize
import com.maestrx.studentcontrol.teacherapp.util.toast
import com.maestrx.studentcontrol.teacherapp.util.toastBlankData
import com.maestrx.studentcontrol.teacherapp.viewmodel.SubjectsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SubjectsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSubjectsBinding.inflate(layoutInflater)

        val viewModel by viewModels<SubjectsViewModel>()

        val adapter = SubjectsAdapter(
            object : SubjectsAdapter.SubjectsListener {
                override fun onEditClickListener(view: View, subject: Subject) {
                    view.isEnabled = false
                    val dialogBinding = DialogEditLineBinding.inflate(inflater)
                    dialogBinding.line.setText(subject.name)
                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle(getString(R.string.change_subject_name))
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
                        val newSubjectName =
                            dialogBinding.line.text.toString().trim().capitalize()
                        if (newSubjectName.isNotBlank()) {
                            viewModel.save(Subject(subject.id, newSubjectName))
                            requireActivity().supportFragmentManager.setFragmentResult(
                                Constants.LESSON_UPDATED,
                                bundleOf()
                            )
                            alertDialog.dismiss()
                        } else {
                            toastBlankData()
                        }
                    }
                }

                override fun onDeleteClickListener(view: View, subject: Subject) {
                    view.isEnabled = false
                    val dialogBinding = DialogMultilineTextBinding.inflate(inflater).apply {
                        line.text = getString(R.string.delete_subject, subject.name)
                    }
                    AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            viewModel.deleteById(subject.id)
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
        binding.subjects.adapter = adapter

        viewModel.state.onEach { state ->
            adapter.submitList(state)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.message
            .onEach { state ->
                when (state.getContentIfNotHandled()) {
                    Constants.MESSAGE_ERROR_SAVING_SUBJECT -> {
                        toast(R.string.error_saving_subject)
                    }

                    Constants.MESSAGE_ERROR_DELETING_SUBJECT -> {
                        toast(R.string.error_deleting_subject)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })

        return binding.root
    }
}