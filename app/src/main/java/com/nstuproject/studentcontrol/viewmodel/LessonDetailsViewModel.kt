package com.nstuproject.studentcontrol.viewmodel

import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nstuproject.studentcontrol.model.ControlStatus
import com.nstuproject.studentcontrol.model.Group
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.repository.lesson.LessonRepository
import com.nstuproject.studentcontrol.repository.lessonGroupCrossRef.LessonGroupCrossRefRepository
import com.nstuproject.studentcontrol.viewmodel.di.LessonDetailsViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = LessonDetailsViewModelFactory::class)
class LessonDetailsViewModel @AssistedInject constructor(
    private val lessonRepository: LessonRepository,
    private val lessonGroupCrossRefRepository: LessonGroupCrossRefRepository,
    @Assisted lesson: Lesson,
) : ViewModel() {

    private val _lessonState = MutableStateFlow(Lesson())
    val lessonState = _lessonState.asStateFlow()

    private val _controlStatus = MutableStateFlow<ControlStatus>(ControlStatus.NotReadyToStart)
    val controlStatus = _controlStatus.asStateFlow()

    private val _wifiReservation = MutableStateFlow<WifiManager.LocalOnlyHotspotReservation?>(null)
    val wifiReservation = _wifiReservation.asStateFlow()

    init {
        setLesson(lesson)
    }

    private fun setLesson(lesson: Lesson) {
        lessonGroupCrossRefRepository.getGroupsByLesson(lesson.id)
            .onEach { groups ->
                val lessonWithGroups =
                    lesson.copy(
                        groups = groups.map {
                            Group.toData(it)
                        }
                            .sortedBy {
                                it.name
                            }
                    )
                _lessonState.update { lessonWithGroups }
            }
            .launchIn(viewModelScope)
    }

    fun deleteLesson() {
        viewModelScope.launch {
            lessonRepository.deleteById(lessonState.value.id)
        }
    }

    fun setControlStatus(status: ControlStatus) {
        _controlStatus.update { status }
    }

    fun setReservation(reservation: WifiManager.LocalOnlyHotspotReservation) {
        _wifiReservation.update { reservation }
    }
}