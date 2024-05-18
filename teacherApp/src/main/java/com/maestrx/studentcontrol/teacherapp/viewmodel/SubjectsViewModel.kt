package com.maestrx.studentcontrol.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.repository.subject.SubjectRepository
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(emptyList<Subject>())
    val state = _state.asStateFlow()

    private val _message = MutableStateFlow(Event(""))
    val message = _message.asStateFlow()

    init {
        subjectRepository.getAll().onEach { list ->
            _state.update {
                list.map {
                    Subject.toData(it)
                }
            }
        }
            .launchIn(viewModelScope)
    }

    fun save(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                subjectRepository.save(subject.toEntity())
            } catch (e: Exception) {
                _message.value = Event(Constants.MESSAGE_ERROR_SAVING_SUBJECT)
                Log.e(Constants.DEBUG_TAG, "Error saving subject: ${e.printStackTrace()}")
            }
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                subjectRepository.deleteById(id)
            } catch (e: Exception) {
                _message.value = Event(Constants.MESSAGE_ERROR_DELETING_SUBJECT)
                Log.e(Constants.DEBUG_TAG, "Error deleting subject: ${e.printStackTrace()}")
            }
        }
    }
}