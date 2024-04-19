package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogEditLineBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentSubjectsBinding
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.recyclerview.subjects.SubjectsAdapter
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.capitalize
import com.maestrx.studentcontrol.teacherapp.utils.toast
import com.maestrx.studentcontrol.teacherapp.utils.toastBlankData
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
                override fun onEditClickListener(subject: Subject) {
                    val dialogBinding = DialogEditLineBinding.inflate(inflater)
                    dialogBinding.line.setText(subject.name)
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.change_subject_name))
                        .setView(dialogBinding.root)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            val newSubjectName =
                                dialogBinding.line.text.toString().trim().capitalize()
                            if (newSubjectName.isNotBlank()) {
                                viewModel.save(Subject(subject.id, newSubjectName))
                            } else {
                                toastBlankData()
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

                override fun onDeleteClickListener(subject: Subject) {
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.delete_subject, subject.name))
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            viewModel.deleteById(subject.id)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
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