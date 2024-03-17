package com.nstuproject.studentcontrol.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ToolbarViewModel : ViewModel() {

    private val _showSettings = MutableStateFlow(false)
    val showSettings = _showSettings.asStateFlow()

    private val _settingsClicked = MutableStateFlow(false)
    val settingsClicked = _settingsClicked.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    fun showSettings(show: Boolean) {
        _showSettings.value = show
    }

    fun settingsClicked(pending: Boolean) {
        _settingsClicked.value = pending
    }

    fun setTitle(title: String) {
        _title.value = title
    }
}