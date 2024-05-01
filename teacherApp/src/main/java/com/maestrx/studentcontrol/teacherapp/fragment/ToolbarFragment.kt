package com.maestrx.studentcontrol.teacherapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogMultilineTextBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentToolbarBinding
import com.maestrx.studentcontrol.teacherapp.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ToolbarFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentFragmentManager.beginTransaction()
            .setPrimaryNavigationFragment(this)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentToolbarBinding.inflate(inflater)

        val navController =
            requireNotNull(childFragmentManager.findFragmentById(R.id.container)).findNavController()
        binding.toolbar.setupWithNavController(navController)

        val viewModel by activityViewModels<ToolbarViewModel>()

        val delete = binding.toolbar.menu.findItem(R.id.delete)
        val save = binding.toolbar.menu.findItem(R.id.save)
        val edit = binding.toolbar.menu.findItem(R.id.edit)
        val dataControl = binding.toolbar.menu.findItem(R.id.dataControl)
        val import = binding.toolbar.menu.findItem(R.id.importStudents)

        save.setOnMenuItemClickListener {
            viewModel.saveClicked(true)
            true
        }

        delete.setOnMenuItemClickListener {
            viewModel.deleteClicked(true)
            true
        }

        edit.setOnMenuItemClickListener {
            viewModel.editClicked(true)
            true
        }

        dataControl.setOnMenuItemClickListener {
            viewModel.dataControlClicked(true)
            true
        }

        import.setOnMenuItemClickListener {
            viewModel.importClicked(true)
            true
        }

        viewModel.showSave
            .onEach {
                save.isVisible = it
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.showDelete
            .onEach {
                delete.isVisible = it
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.showEdit
            .onEach {
                edit.isVisible = it
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.showDataControl
            .onEach {
                dataControl.isVisible = it
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.showImport
            .onEach {
                import.isVisible = it
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.title
            .onEach { state ->
                if (state.isNotBlank()) {
                    binding.toolbar.title = state
                    viewModel.setTitle("")
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.isControlRunning
            .onEach { state ->
                if (state) {
                    edit.isEnabled = false
                    delete.isEnabled = false
                    edit.icon?.setTint(getColor(requireContext(), R.color.disabled_button))
                    delete.icon?.setTint(getColor(requireContext(), R.color.disabled_button))
                } else {
                    edit.isEnabled = true
                    delete.isEnabled = true
                    edit.icon?.setTint(getColor(requireContext(), R.color.BW))
                    delete.icon?.setTint(getColor(requireContext(), R.color.BW))
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.toolbar.setNavigationOnClickListener {
            val currentFragment = navController.currentBackStackEntry?.destination?.id
            if (currentFragment == R.id.lessonDetailsFragment && viewModel.isControlRunning.value) {
                val dialogBinding = DialogMultilineTextBinding.inflate(inflater)
                dialogBinding.line.text = getString(R.string.quit_from_control)
                AlertDialog.Builder(context)
                    .setView(dialogBinding.root)
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        navController.navigateUp()
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                navController.navigateUp()
            }
        }

        return binding.root
    }
}