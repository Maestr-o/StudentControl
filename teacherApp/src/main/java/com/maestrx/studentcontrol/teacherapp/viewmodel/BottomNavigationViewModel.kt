package com.maestrx.studentcontrol.teacherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.db.AppDb
import com.maestrx.studentcontrol.teacherapp.excel.ExcelManager
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomNavigationViewModel @Inject constructor(
    private val db: AppDb,
    private val sm: ExcelManager,
) : ViewModel() {

    private val _message = MutableStateFlow(Event(""))
    val message = _message.asStateFlow()

    fun exportToExcel() {
        viewModelScope.launch {
            _message.value = if (sm.export()) {
                Event(Constants.MESSAGE_END_EXPORT)
            } else {
                Event(Constants.MESSAGE_ERROR_EXPORT)
            }
        }
    }

    fun cleanDatabase() {
        _message.value = try {
            db.clearAllTables()
            Event(Constants.MESSAGE_OK_DELETING_ALL_DATA)
        } catch (e: Exception) {
            Event(Constants.MESSAGE_ERROR_DELETING_ALL_DATA)
        }
    }
}