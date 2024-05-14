package com.maestrx.studentcontrol.teacherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.Mark
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.mark.MarkRepository
import com.maestrx.studentcontrol.teacherapp.repository.subject.SubjectRepository
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.ReportViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ReportViewModelFactory::class)
class ReportViewModel @AssistedInject constructor(
    private val subjectRepository: SubjectRepository,
    private val lessonRepository: LessonRepository,
    private val markRepository: MarkRepository,
    @Assisted private val student: Student,
) : ViewModel() {

    private val _subjectState = MutableStateFlow(ReportSubjectState())
    val subjectState = _subjectState.asStateFlow()

    private val _reportState = MutableStateFlow(ReportUiState())
    val reportState = _reportState.asStateFlow()

    init {
        viewModelScope.launch {
            _subjectState.update {
                ReportSubjectState(subjects = getSubjects())
            }
        }

        subjectState
            .onEach {
                _reportState.update {
                    formReportState()
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun formReportState(): ReportUiState {
        val lessons = getLessons()
        val marks = getMarks()
        val percentage = getPercentage()
        return ReportUiState(student, lessons, marks, percentage)
    }

    private suspend fun getSubjects(): List<Subject> =
        subjectRepository.getByGroupId(student.group.id).map {
            Subject.toData(it)
        }

    private suspend fun getLessons(): List<Lesson> =
        lessonRepository.getBySubjectIdAndGroupId(
            subjectState.value.subjects[subjectState.value.selSubject].id,
            student.group.id
        )
            .map {
                Lesson.fromResponseToData(it)
            }

    private suspend fun getMarks(): List<Mark> =
        markRepository.getByStudentIdAndSubjectId(
            student.id,
            subjectState.value.subjects[subjectState.value.selSubject].id
        ).map {
            Mark.toData(it)
        }

    private fun getPercentage(): Float {
        val lessonsCount = reportState.value.lessons.count()
        val marksCount = reportState.value.marks.count()
        return (marksCount.toFloat() / lessonsCount * 100)
    }

    fun setSubject(subjectId: Int) {
        _subjectState.update {
            it.copy(selSubject = subjectId)
        }
    }
}