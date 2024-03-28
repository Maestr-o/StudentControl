package com.nstuproject.studentcontrol.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentLessonDetailsBinding
import com.nstuproject.studentcontrol.model.ControlStatus
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonType
import com.nstuproject.studentcontrol.recyclerview.groupsSelected.GroupSelectedAdapter
import com.nstuproject.studentcontrol.utils.Constants
import com.nstuproject.studentcontrol.utils.TimeFormatter
import com.nstuproject.studentcontrol.utils.isPermissionGranted
import com.nstuproject.studentcontrol.utils.toast
import com.nstuproject.studentcontrol.viewmodel.LessonDetailsViewModel
import com.nstuproject.studentcontrol.viewmodel.ToolbarViewModel
import com.nstuproject.studentcontrol.viewmodel.di.LessonDetailsViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@AndroidEntryPoint
class LessonDetailsFragment : Fragment() {

    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()
    private lateinit var _viewModel: Lazy<LessonDetailsViewModel>

    private lateinit var lessonArg: Lesson

    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var fLocationClient: FusedLocationProviderClient

    override fun onStart() {
        super.onStart()
        toolbarViewModel.showDelete(true)
        toolbarViewModel.showEdit(true)
    }

    override fun onStop() {
        super.onStop()
        toolbarViewModel.showDelete(false)
        toolbarViewModel.showEdit(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLessonDetailsBinding.inflate(inflater, container, false)

        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        lessonArg = arguments?.let {
            Json.decodeFromString<Lesson>(
                it.getString(Constants.LESSON_DATA) ?: Lesson().toString()
            )
        } ?: Lesson()

        _viewModel = viewModels<LessonDetailsViewModel>(
            extrasProducer = {
                defaultViewModelCreationExtras.withCreationCallback<LessonDetailsViewModelFactory> { factory ->
                    factory.create(lessonArg)
                }
            }
        )
        val viewModel = _viewModel.value

        val groupsAdapter = GroupSelectedAdapter()
        binding.groups.adapter = groupsAdapter

        toolbarViewModel.editClicked
            .onEach {
                if (toolbarViewModel.editClicked.value) {
                    val bundle = Bundle().apply {
                        putString(
                            Constants.LESSON_DATA,
                            Json.encodeToString(viewModel.lessonState.value)
                        )
                    }
                    findNavController().navigate(
                        R.id.action_lessonDetailsFragment_to_editLessonFragment,
                        bundle
                    )
                    toolbarViewModel.editClicked(false)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        toolbarViewModel.deleteClicked
            .onEach {
                if (toolbarViewModel.deleteClicked.value) {
                    AlertDialog.Builder(context)
                        .setTitle(R.string.ask_delete_lesson)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            viewModel.deleteLesson()
                            requireActivity().supportFragmentManager.setFragmentResult(
                                Constants.LESSON_UPDATED,
                                bundleOf()
                            )
                            findNavController().navigateUp()
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    toolbarViewModel.deleteClicked(false)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.lessonState
            .onEach { state ->
                groupsAdapter.submitList(state.groups)
                binding.apply {
                    subject.text = state.subject.name
                    datetime.text = getString(
                        R.string.lesson_details_datetime,
                        TimeFormatter.unixTimeToDateString(state.timeStart),
                        TimeFormatter.unixTimeToTimeString(state.timeStart),
                        TimeFormatter.unixTimeToTimeString(state.timeEnd)
                    )
                    auditory.text = state.auditory
                    type.text = when (state.type.name) {
                        LessonType.LECTURE.toString() -> {
                            root.context.getString(R.string.lecture)
                        }

                        LessonType.PRACTICE.toString() -> {
                            root.context.getString(R.string.practice)
                        }

                        LessonType.LAB.toString() -> {
                            root.context.getString(R.string.laboratory_work)
                        }

                        else -> {
                            root.context.getString(R.string.lesson)
                        }
                    }
                    title.text = state.title
                    if (state.description.isNotBlank()) {
                        notesLayout.isVisible = true
                        notes.text = state.description
                    } else {
                        notesLayout.isGone = true
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        lifecycleScope.launch {
            while (true) {
                checkTime()
                delay(Constants.TIME_CHECK_DELAY)
            }
        }

        viewModel.controlStatus
            .onEach { state ->
                when (state) {
                    is ControlStatus.NotReadyToStart -> {
                        toolbarViewModel.startControl(false)
                        binding.apply {
                            startControl.isEnabled = false
                            startControl.text = getString(R.string.early_control)
                            registeredCount.isGone = true
                            students.isGone = true
                            ssid.isGone = true
                        }
                    }

                    ControlStatus.ReadyToStart -> {
                        toolbarViewModel.startControl(false)
                        checkPermission()
                        binding.apply {
                            startControl.isEnabled = true
                            startControl.text = getString(R.string.start_control)
                            registeredCount.isGone = true
                            students.isGone = true
                            ssid.isGone = true
                        }
                    }

                    ControlStatus.Starting -> {
                        toolbarViewModel.startControl(true)
                        binding.apply {
                            startControl.isEnabled = false
                            startControl.text = getString(R.string.starting_control)
                            registeredCount.isGone = true
                            students.isGone = true
                            ssid.isGone = true
                        }
                        turnOnHotspot()
                    }

                    ControlStatus.Running -> {
                        toolbarViewModel.startControl(true)
                        binding.apply {
                            startControl.isEnabled = true
                            startControl.text = getString(R.string.stop_control)
                            registeredCount.isVisible = true
                            students.isVisible = true
                            ssid.isVisible = true
                            ssid.text = getString(
                                R.string.ap_ssid,
                                viewModel.wifiReservation.value?.wifiConfiguration?.SSID
                            )
                        }
                    }

                    ControlStatus.Stopping -> {
                        toolbarViewModel.startControl(true)
                        binding.apply {
                            startControl.isEnabled = false
                            startControl.text = getString(R.string.stopping_control)
                            registeredCount.isVisible = true
                            students.isVisible = true
                            ssid.isVisible = true
                        }
                        turnOffHotspot()
                    }

                    ControlStatus.Finished -> {
                        toolbarViewModel.startControl(false)
                        binding.apply {
                            startControl.isEnabled = false
                            startControl.text = getString(R.string.end_control)
                            registeredCount.isVisible = true
                            students.isVisible = true
                            ssid.isGone = true
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.startControl.setOnClickListener {
            when (viewModel.controlStatus.value) {
                is ControlStatus.ReadyToStart -> {
                    viewModel.setControlStatus(ControlStatus.Starting)
                }

                ControlStatus.Running -> {
                    viewModel.setControlStatus(ControlStatus.Stopping)
                }

                else -> {}
            }
        }

        return binding.root
    }

    private fun checkTime() {
        val viewModel = _viewModel.value
        val status = viewModel.controlStatus.value
        if (status is ControlStatus.ReadyToStart || status is ControlStatus.NotReadyToStart
            || status is ControlStatus.Stopping
        ) {
            val lessonVM = viewModel.lessonState.value
            val lesson = if (lessonVM.timeStart == 0L) {
                lessonArg
            } else {
                lessonVM
            }
            val startTime = TimeFormatter.decRecess(lesson.timeStart)
            val endTime = lesson.timeEnd
            val time = System.currentTimeMillis()

            if (time in startTime..endTime) {
                viewModel.setControlStatus(ControlStatus.ReadyToStart)
            } else if (time < startTime) {
                viewModel.setControlStatus(ControlStatus.NotReadyToStart)
            } else if (time > endTime) {
                viewModel.setControlStatus(ControlStatus.Finished)
            }
        }
    }

    private fun turnOnHotspot() {
        val viewModel = _viewModel.value
        checkPermission()

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkNearbyDevicesPermission())
            || (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && checkFineLocationPermission())
        ) {
            createAP()
        } else {
            viewModel.setControlStatus(ControlStatus.ReadyToStart)
            toast(R.string.cant_create_ap)
        }
    }

    private fun turnOffHotspot() {
        _viewModel.value.turnOffHotspot()
        toast(R.string.ap_stopped)
    }

    @SuppressLint("MissingPermission")
    private fun createAP() {
        val viewModel = _viewModel.value
        (requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
            .startLocalOnlyHotspot(object : LocalOnlyHotspotCallback() {

                override fun onStarted(reservation: LocalOnlyHotspotReservation) {
                    super.onStarted(reservation)
                    toast(R.string.ap_started)
                    viewModel.setReservation(reservation)
                    viewModel.setControlStatus(ControlStatus.Running)
                }

                override fun onStopped() {
                    super.onStopped()
                    toast(R.string.ap_stopped)
                    checkTime()
                }

                override fun onFailed(reason: Int) {
                    super.onFailed(reason)
                    toast(R.string.ap_error)
                }
            }, Handler())
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNearbyDevicesPermission(): Boolean =
        isPermissionGranted(Manifest.permission.NEARBY_WIFI_DEVICES)

    private fun checkFineLocationPermission(): Boolean =
        isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun checkPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!checkNearbyDevicesPermission()) {
                    pLauncher =
                        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
                    pLauncher.launch(Manifest.permission.NEARBY_WIFI_DEVICES)
                }
            } else {
                if (!checkFineLocationPermission()) {
                    pLauncher =
                        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
                    pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        } catch (e: Exception) {
            Log.d("TEST", e.toString())
        }
    }
}