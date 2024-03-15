package com.nstuproject.studentcontrol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.db.entity.GroupEntity
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.repository.group.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(emptyList<Group>())
    val state = _state.asStateFlow()

    init {
        groupRepository.getAll().onEach { list ->
            _state.update {
                list.map {
                    it.toData()
                }
            }
        }
            .launchIn(viewModelScope)
    }

    fun save(group: Group) {
        viewModelScope.launch {
            try {
                groupRepository.save(GroupEntity.toEntity(group))
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            try {
                groupRepository.deleteById(id)
            } catch (e: Exception) {
                Log.e("TEST", e.toString())
            }
        }
    }
}