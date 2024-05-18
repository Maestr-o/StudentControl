package com.maestrx.studentcontrol.teacherapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.db.DbFileManager
import com.maestrx.studentcontrol.teacherapp.excel.ExcelManager
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomNavigationViewModel @Inject constructor(
    private val excelManager: ExcelManager,
    private val dbFileManager: DbFileManager,
) : ViewModel() {

    private val _message = MutableStateFlow(Event(""))
    val message = _message.asStateFlow()

    fun exportToExcel() {
        viewModelScope.launch {
            _message.value = if (excelManager.export()) {
                Event(Constants.MESSAGE_OK_EXPORT)
            } else {
                Event(Constants.MESSAGE_ERROR_EXPORT)
            }
        }
    }

    fun exportDb() {
        viewModelScope.launch(Dispatchers.Default) {
            _message.value = try {
                dbFileManager.export()
                Event(Constants.MESSAGE_OK_EXPORT)
            } catch (e: Exception) {
                Log.d(Constants.DEBUG_TAG, "Export error: ${e.printStackTrace()}")
                Event(Constants.MESSAGE_ERROR_EXPORT)
            }
        }
    }

    fun importDb(uri: Uri?) {
        viewModelScope.launch {
            _message.value = try {
                dbFileManager.import(requireNotNull(uri))
                Event(Constants.MESSAGE_OK_IMPORT)
            } catch (e: Exception) {
                Log.d(Constants.DEBUG_TAG, "Import error: ${e.printStackTrace()}")
                Event(Constants.MESSAGE_ERROR_IMPORT)
            }
        }
    }

    fun cleanDatabase() {
        _message.value = try {
            dbFileManager.clean()
            Event(Constants.MESSAGE_OK_DELETING_ALL_DATA)
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_TAG, "DB clean error: ${e.printStackTrace()}")
            Event(Constants.MESSAGE_ERROR_DELETING_ALL_DATA)
        }
    }
}