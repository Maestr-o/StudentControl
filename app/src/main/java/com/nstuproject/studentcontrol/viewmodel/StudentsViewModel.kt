package com.nstuproject.studentcontrol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.db.entity.StudentEntity
import com.nstuproject.studentcontrol.model.Student
import com.nstuproject.studentcontrol.repository.student.StudentRepository
import com.nstuproject.studentcontrol.viewmodel.di.StudentsViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        studentRepository.getStudentsByGroup(groupId).onEach { list ->
            _state.update {
                list.map {
                    it.toData()
                }
            }
        }
            .launchIn(viewModelScope)
    }

    fun save(student: Student) {
        viewModelScope.launch {
            try {
                studentRepository.save(StudentEntity.toEntity(student))
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            try {
                studentRepository.deleteById(id)
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }
    }
}