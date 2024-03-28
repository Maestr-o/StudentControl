package com.nstuproject.studentcontrol.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.DialogEditLineBinding
import com.nstuproject.studentcontrol.databinding.FragmentGroupsBinding
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.recyclerview.groups.GroupsAdapter
import com.nstuproject.studentcontrol.utils.Constants
import com.nstuproject.studentcontrol.utils.toastBlankData
import com.nstuproject.studentcontrol.viewmodel.GroupsViewModel
import com.nstuproject.studentcontrol.viewmodel.ToolbarViewModel
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

        val toolbarViewModel by activityViewModels<ToolbarViewModel>()
        val viewModel by viewModels<GroupsViewModel>()

        val adapter = GroupsAdapter(
            object : GroupsAdapter.GroupsListener {
                override fun onClickListener(group: Group) {
                    val bundle = Bundle().apply {
                        putLong(Constants.GROUP_ID, group.id)
                    }
                    toolbarViewModel.setTitle(group.name)
                    requireParentFragment().requireParentFragment().findNavController()
                        .navigate(R.id.action_bottomNavigationFragment_to_studentsFragment, bundle)
                }

                override fun onEditClickListener(group: Group) {
                    val dialogBinding = DialogEditLineBinding.inflate(inflater)
                    dialogBinding.line.setText(group.name)
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.change_group_s_name))
                        .setView(dialogBinding.root)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            val newSubjectName = dialogBinding.line.text.toString().trim()
                            if (newSubjectName.isNotBlank()) {
                                viewModel.save(
                                    group.copy(name = newSubjectName)
                                )
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

                override fun onDeleteClickListener(group: Group) {
                    AlertDialog.Builder(context)
                        .setTitle(getString(R.string.delete_group, group.name))
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
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