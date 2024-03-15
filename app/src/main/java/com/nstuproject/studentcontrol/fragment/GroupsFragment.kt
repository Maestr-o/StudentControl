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
import com.nstuproject.studentcontrol.databinding.FragmentGroupsBinding
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.recyclerview.groups.GroupsAdapter
import com.nstuproject.studentcontrol.viewmodel.GroupsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class GroupsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGroupsBinding.inflate(layoutInflater)

        val viewModel by viewModels<GroupsViewModel>()

        val adapter = GroupsAdapter(
            object : GroupsAdapter.GroupsListener {
                override fun onEditClickListener(group: Group) {
                    val editText = EditText(context)
                    editText.setText(group.name)
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.change_group_s_name))
                        .setView(editText)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            val newSubjectName = editText.text.toString()
                            viewModel.save(Group(group.id, newSubjectName))
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

                override fun onDeleteClickListener(group: Group) {
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.delete_group, group.name))
                        .setPositiveButton("OK") { dialog, _ ->
                            viewModel.deleteById(group.id)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        )
        binding.groups.adapter = adapter

        viewModel.state.onEach { state ->
            adapter.submitList(state)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}