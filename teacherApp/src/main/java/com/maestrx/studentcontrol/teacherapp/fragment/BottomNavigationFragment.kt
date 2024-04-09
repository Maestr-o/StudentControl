package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogEditLineBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentBottomNavigationBinding
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.utils.toastBlankData
import com.maestrx.studentcontrol.teacherapp.viewmodel.GroupsViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.SubjectsViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavigationFragment : Fragment() {

    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomNavigationBinding.inflate(inflater)

        val subjectsViewModel by viewModels<SubjectsViewModel>()
        val groupsViewModel by viewModels<GroupsViewModel>()

        val navController =
            requireNotNull(childFragmentManager.findFragmentById(R.id.container)).findNavController()
        binding.bottomNavigation.setupWithNavController(navController)

        val newLessonListener = View.OnClickListener {
            (requireNotNull(
                childFragmentManager.findFragmentById(R.id.container)?.childFragmentManager?.findFragmentById(
                    R.id.container
                )
            ) as LessonsFragment).addLesson()
        }

        val newSubjectListener = View.OnClickListener {
            val dialogBinding = DialogEditLineBinding.inflate(inflater)
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.add_new_subject))
                .setView(dialogBinding.root)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    val newSubjectName = dialogBinding.line.text.toString().trim()
                    if (newSubjectName.isNotBlank()) {
                        subjectsViewModel.save(
                            Subject(name = newSubjectName)
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

        val newGroupListener = View.OnClickListener {
            val dialogBinding = DialogEditLineBinding.inflate(inflater)
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.add_new_group))
                .setView(dialogBinding.root)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    val newGroupName = dialogBinding.line.text.toString().trim()
                    if (newGroupName.isNotBlank()) {
                        groupsViewModel.save(
                            Group(name = newGroupName)
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

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.lessonsFragment -> {
                    binding.add.setOnClickListener(newLessonListener)
                }

                R.id.subjectsFragment -> {
                    binding.add.setOnClickListener(newSubjectListener)
                }

                R.id.groupsFragment -> {
                    binding.add.setOnClickListener(newGroupListener)
                }
            }
        }

        return binding.root
    }
}