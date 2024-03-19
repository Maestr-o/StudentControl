package com.nstuproject.studentcontrol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.db.entity.SubjectEntity
import com.nstuproject.studentcontrol.model.Subject
import com.nstuproject.studentcontrol.repository.subject.SubjectRepository
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

    init {
        subjectRepository.getAll().onEach { list ->
            _state.update {
                list.map {
                    it.toData()
                }
            }
        }
            .launchIn(viewModelScope)
    }

    fun save(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                subjectRepository.save(SubjectEntity.toEntity(subject))
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                subjectRepository.deleteById(id)
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }
    }
}