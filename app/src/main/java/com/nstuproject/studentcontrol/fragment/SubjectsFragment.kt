package com.nstuproject.studentcontrol.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentSubjectsBinding
import com.nstuproject.studentcontrol.model.Subject
import com.nstuproject.studentcontrol.recyclerview.subjects.SubjectsAdapter
import com.nstuproject.studentcontrol.viewmodel.SubjectsViewModel
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
                    val editText = EditText(context)
                    editText.setText(subject.name)
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.change_subject_name))
                        .setView(editText)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            val newSubjectName = editText.text.toString()
                            viewModel.save(Subject(subject.id, newSubjectName))
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
                        .setPositiveButton("OK") { dialog, _ ->
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

        return binding.root
    }
}