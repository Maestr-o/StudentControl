package com.maestrx.studentcontrol.teacherapp.fragment

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
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogEditLineBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogMultilineTextBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentGroupsBinding
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.recyclerview.groups.GroupsAdapter
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.toast
import com.maestrx.studentcontrol.teacherapp.util.toastBlankData
import com.maestrx.studentcontrol.teacherapp.viewmodel.GroupsViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.ToolbarViewModel
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

                override fun onEditClickListener(view: View, group: Group) {
                    view.isEnabled = false
                    val dialogBinding = DialogEditLineBinding.inflate(inflater)
                    dialogBinding.line.setText(group.name)
                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle(getString(R.string.change_group_s_name))
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
                        val newSubjectName = dialogBinding.line.text.toString().trim()
                        if (newSubjectName.isNotBlank()) {
                            viewModel.save(group.copy(name = newSubjectName))
                            alertDialog.dismiss()
                        } else {
                            toastBlankData()
                        }
                    }
                }

                override fun onDeleteClickListener(view: View, group: Group) {
                    view.isEnabled = false
                    val dialogBinding = DialogMultilineTextBinding.inflate(inflater).apply {
                        line.text = getString(R.string.delete_group, group.name)
                    }
                    AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            viewModel.deleteById(group.id)
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
        binding.groups.adapter = adapter

        viewModel.state.onEach { state ->
            adapter.submitList(state)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.message
            .onEach { event ->
                when (event.getContentIfNotHandled()) {
                    Constants.MESSAGE_ERROR_SAVING_GROUP -> {
                        toast(R.string.error_saving_group)
                    }

                    Constants.MESSAGE_ERROR_DELETING_GROUP -> {
                        toast(R.string.error_deleting_group)
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