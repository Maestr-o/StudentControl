package com.nstuproject.studentcontrol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.model.Subject
import com.nstuproject.studentcontrol.repository.subject.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(emptyList<Subject>())
    val state = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _state.update {
                    subjectRepository.getAll()
                }
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }
    }

    fun save(subject: Subject) {
        viewModelScope.launch {
            try {
                subjectRepository.save(subject)
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }

        load()
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            try {
                subjectRepository.deleteById(id)
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }

        load()
    }
}