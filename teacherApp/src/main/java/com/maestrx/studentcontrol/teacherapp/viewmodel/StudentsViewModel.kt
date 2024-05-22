package com.maestrx.studentcontrol.teacherapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.excel.ExcelManager
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.Event
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.StudentsViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = StudentsViewModelFactory::class)
class StudentsViewModel @AssistedInject constructor(
    private val studentRepository: StudentRepository,
    lessonRepository: LessonRepository,
    private val excelManager: ExcelManager,
    @Assisted val groupId: Long,
) : ViewModel() {

    private val _studentsState = MutableStateFlow(emptyList<Student>())
    val studentsState = _studentsState.asStateFlow()

    private val _lessonsCount = MutableStateFlow(0L)
    val lessonsCount = _lessonsCount.asStateFlow()

    private val _fileState = MutableStateFlow<Uri?>(null)
    val fileState = _fileState.asStateFlow()

    private val _importState = MutableStateFlow(StudentsImportUiState())
    val importState = _importState.asStateFlow()

    private val _message = MutableStateFlow(Event(""))
    val message = _message.asStateFlow()

    init {
        studentRepository.getByGroupId(groupId).onEach { list ->
            _studentsState.update {
                list.map {
                    Student.fromResponseToData(it)
                }
            }
        }
            .launchIn(viewModelScope)

        lessonRepository.getCount().onEach { count ->
            _lessonsCount.update { count }
        }
            .launchIn(viewModelScope)

        fileState.onEach { uri ->
            if (uri != null) {
                _importState.update {
                    it.copy(tableNames = excelManager.getExcelTableNames(uri))
                }
            }
        }
            .launchIn(viewModelScope)
    }

    fun save(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                studentRepository.save(student.toEntity())
            } catch (e: Exception) {
                _message.value = Event(Constants.MESSAGE_ERROR_SAVING_STUDENT)
                Log.e(Constants.DEBUG_TAG, "Error saving student: $e")
            }
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                studentRepository.deleteById(id)
            } catch (e: Exception) {
                _message.value = Event(Constants.MESSAGE_ERROR_DELETING_STUDENT)
                Log.e(Constants.DEBUG_TAG, "Error deleting student: $e")
            }
        }
    }

    fun selectFile(uri: Uri) {
        _fileState.update { uri }
    }

    fun saveImportState(state: StudentsImportUiState?) = with(state) {
        if (this != null) {
            _importState.update {
                it.copy(
                    selectedTable = selectedTable,
                    column = column,
                    startX = startX,
                    endX = endX,
                )
            }
        }
    }

    fun importStudents(groupId: Long) {
        viewModelScope.launch {
            try {
                val students = requireNotNull(
                    importState.value.run {
                        excelManager.importStudents(
                            requireNotNull(fileState.value),
                            selectedTable,
                            groupId,
                            column,
                            startX.toInt(),
                            endX.toInt(),
                        )
                    }
                )
                require(students.isNotEmpty())
                studentRepository.saveList(students)
                _message.value = Event(Constants.MESSAGE_OK_IMPORT)
            } catch (e: Exception) {
                Log.d(Constants.DEBUG_TAG, "Import error: $e")
                _message.value = Event(Constants.MESSAGE_ERROR_IMPORT)
            }
        }
    }

    fun cleanImportState() {
        _fileState.update { null }
        _importState.update { StudentsImportUiState() }
    }
}