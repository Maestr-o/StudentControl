package com.nstuproject.studentcontrol.viewmodel

import androidx.lifecycle.ViewModel
import com.nstuproject.studentcontrol.viewmodel.di.SettingsViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = SettingsViewModelFactory::class)
class SettingsViewModel @AssistedInject constructor(
    @Assisted val ssid: String,
) : ViewModel() {

    private val _ssidState = MutableStateFlow("")
    val ssidState = _ssidState.asStateFlow()

    private val _permissionStatus = MutableStateFlow(false)
    val permissionStatus = _permissionStatus.asStateFlow()

    init {
        setState(ssid)
    }

    fun setState(name: String) {
        _ssidState.update { name }
    }

    fun setPermissionStatus(status: Boolean) {
        _permissionStatus.update { status }
    }
}