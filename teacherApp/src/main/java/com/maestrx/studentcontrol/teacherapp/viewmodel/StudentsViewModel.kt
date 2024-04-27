package com.maestrx.studentcontrol.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.Event
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
    @Assisted private val groupId: Long,
) : ViewModel() {

    private val _state = MutableStateFlow(emptyList<Student>())
    val state = _state.asStateFlow()

    private val _message = MutableStateFlow(Event(""))
    val message = _message.asStateFlow()

    init {
        studentRepository.getByGroupId(groupId).onEach { list ->
            _state.update {
                list.map {
                    Student.fromResponseToData(it)
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
}