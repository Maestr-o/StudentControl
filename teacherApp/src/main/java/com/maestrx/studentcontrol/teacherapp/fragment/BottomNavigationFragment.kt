package com.maestrx.studentcontrol.teacherapp.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.DialogDataControlBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogEditLineBinding
import com.maestrx.studentcontrol.teacherapp.databinding.DialogMultilineTextBinding
import com.maestrx.studentcontrol.teacherapp.databinding.FragmentBottomNavigationBinding
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.capitalize
import com.maestrx.studentcontrol.teacherapp.utils.toast
import com.maestrx.studentcontrol.teacherapp.utils.toastBlankData
import com.maestrx.studentcontrol.teacherapp.viewmodel.BottomNavigationViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.GroupsViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.SubjectsViewModel
import com.maestrx.studentcontrol.teacherapp.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class BottomNavigationFragment : Fragment() {

    private val viewModel by viewModels<BottomNavigationViewModel>()
    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()

    private lateinit var pLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPermissionListener()
    }

    override fun onStart() {
        super.onStart()
        toolbarViewModel.showDataControl(true)
    }

    override fun onStop() {
        super.onStop()
        toolbarViewModel.showDataControl(false)
    }

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
            (
                    requireNotNull(
                        childFragmentManager.findFragmentById(R.id.container)?.childFragmentManager
                            ?.findFragmentById(R.id.container)
                    ) as LessonsFragment).addLesson()
        }

        val newSubjectListener = View.OnClickListener {
            binding.add.isEnabled = false
            val dialogBinding = DialogEditLineBinding.inflate(inflater)
            val alertDialog = AlertDialog.Builder(context)
                .setTitle(getString(R.string.add_new_subject))
                .setView(dialogBinding.root)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    binding.add.isEnabled = true
                }
                .show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val newSubjectName = dialogBinding.line.text.toString().trim().capitalize()
                if (newSubjectName.isNotBlank()) {
                    subjectsViewModel.save(Subject(name = newSubjectName))
                    alertDialog.dismiss()
                } else {
                    toastBlankData()
                }
            }
        }

        val newGroupListener = View.OnClickListener {
            binding.add.isEnabled = false
            val dialogBinding = DialogEditLineBinding.inflate(inflater)
            val alertDialog = AlertDialog.Builder(context)
                .setTitle(getString(R.string.add_new_group))
                .setView(dialogBinding.root)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    binding.add.isEnabled = true
                }
                .show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val newGroupName = dialogBinding.line.text.toString().trim()
                if (newGroupName.isNotBlank()) {
                    groupsViewModel.save(Group(name = newGroupName))
                    alertDialog.dismiss()
                } else {
                    toastBlankData()
                }
            }
        }

        var dataControlDialog: AlertDialog? = null
        toolbarViewModel.dataControlClicked
            .onEach { state ->
                if (state) {
                    val dialogBinding = DialogDataControlBinding.inflate(inflater).apply {
                        clean.setOnClickListener {
                            val sureDialogBinding =
                                DialogMultilineTextBinding.inflate(inflater).apply {
                                    line.text = getString(R.string.sure_clean_data)
                                }
                            AlertDialog.Builder(context)
                                .setView(sureDialogBinding.root)
                                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                    viewModel.cleanDatabase()
                                    dataControlDialog?.dismiss()
                                    dialog.dismiss()
                                }
                                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }

                        exportExcel.setOnClickListener {
                            if (checkPermission()) {
                                progressBar.isVisible = true
                                controlContainer.isGone = true
                                viewModel.exportToExcel()
                            }
                        }

                        exportDb.setOnClickListener {
                            if (checkPermission()) {
                                progressBar.isVisible = true
                                controlContainer.isGone = true
                                viewModel.exportDb()
                            }
                        }

                        importDb.setOnClickListener {
                            progressBar.isVisible = true
                            controlContainer.isGone = true
                            viewModel.importDb()
                        }
                    }
                    dataControlDialog = AlertDialog.Builder(context)
                        .setTitle(getString(R.string.data_control))
                        .setView(dialogBinding.root)
                        .setNegativeButton(getString(R.string.back)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setOnDismissListener {
                            toolbarViewModel.dataControlClicked(false)
                        }
                        .show()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.message
            .onEach { event ->
                when (event.getContentIfNotHandled()) {
                    Constants.MESSAGE_ERROR_EXPORT -> {
                        toast(R.string.error_export)
                    }

                    Constants.MESSAGE_END_EXPORT -> {
                        toast(R.string.ok_creating_file)
                    }

                    Constants.MESSAGE_OK_DELETING_ALL_DATA -> {
                        toast(R.string.ok_data_clear)
                    }

                    Constants.MESSAGE_ERROR_DELETING_ALL_DATA -> {
                        toast(R.string.error_data_clear)
                    }

                    Constants.MESSAGE_ERROR_IMPORT -> {
                        toast(R.string.import_error)
                    }
                }
                dataControlDialog?.dismiss()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

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

    private fun checkPermission(): Boolean {
        when {
            Build.VERSION.SDK_INT > 28 -> return true

            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                return true
            }

            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                permissionAlert()
            }

            else -> {
                pLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        return false
    }

    private fun registerPermissionListener() {
        pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    permissionAlert()
                }
            }
    }

    private fun permissionAlert() {
        val dialogBinding = DialogMultilineTextBinding.inflate(layoutInflater).apply {
            line.text = getString(R.string.need_files_permission)
        }
        AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                pLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}