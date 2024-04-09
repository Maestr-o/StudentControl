package com.maestrx.studentcontrol.teacherapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ToolbarViewModel : ViewModel() {

    private val _showSave = MutableStateFlow(false)
    val showSave = _showSave.asStateFlow()

    private val _settingsClicked = MutableStateFlow(false)
    val settingsClicked = _settingsClicked.asStateFlow()

    private val _saveClicked = MutableStateFlow(false)
    val saveClicked = _saveClicked.asStateFlow()

    private val _showEdit = MutableStateFlow(false)
    val showEdit = _showEdit.asStateFlow()

    private val _editClicked = MutableStateFlow(false)
    val editClicked = _editClicked.asStateFlow()

    private val _showDelete = MutableStateFlow(false)
    val showDelete = _showDelete.asStateFlow()

    private val _deleteClicked = MutableStateFlow(false)
    val deleteClicked = _deleteClicked.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _isStartedControl = MutableStateFlow(false)
    val isStartedControl = _isStartedControl.asStateFlow()

    fun showSave(show: Boolean) {
        _showSave.value = show
    }

    fun showDelete(show: Boolean) {
        _showDelete.value = show
    }

    fun showEdit(show: Boolean) {
        _showEdit.value = show
    }

    fun settingsClicked(pending: Boolean) {
        _settingsClicked.value = pending
    }

    fun saveClicked(pending: Boolean) {
        _saveClicked.value = pending
    }

    fun deleteClicked(pending: Boolean) {
        _deleteClicked.value = pending
    }

    fun editClicked(pending: Boolean) {
        _editClicked.value = pending
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun startControl(flag: Boolean) {
        _isStartedControl.update { flag }
    }
}