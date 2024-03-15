package com.nstuproject.studentcontrol.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentBottomNavigationBinding
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.model.Subject
import com.nstuproject.studentcontrol.viewmodel.GroupsViewModel
import com.nstuproject.studentcontrol.viewmodel.SubjectsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavigationFragment : Fragment() {

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

        val newSubjectListener = View.OnClickListener {
            val editText = EditText(context)
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.add_new_subject))
                .setView(editText)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    val newSubjectName = editText.text.toString()
                    subjectsViewModel.save(Subject(0, newSubjectName))
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        val newGroupListener = View.OnClickListener {
            val editText = EditText(context)
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.add_new_group))
                .setView(editText)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    val newGroupName = editText.text.toString()
                    groupsViewModel.save(Group(0, newGroupName))
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