package com.maestrx.studentcontrol.teacherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.Mark
import com.maestrx.studentcontrol.teacherapp.model.ReportLesson
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.mark.MarkRepository
import com.maestrx.studentcontrol.teacherapp.repository.subject.SubjectRepository
import com.maestrx.studentcontrol.teacherapp.utils.TimeFormatter
import com.maestrx.studentcontrol.teacherapp.viewmodel.di.ReportViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ReportViewModelFactory::class)
class ReportViewModel @AssistedInject constructor(
    private val subjectRepository: SubjectRepository,
    private val lessonRepository: LessonRepository,
    private val markRepository: MarkRepository,
    @Assisted private val student: Student,
) : ViewModel() {

    private val _subjectState = MutableStateFlow<List<Subject>>(emptyList())
    val subjectState = _subjectState.asStateFlow()

    private val _reportState = MutableStateFlow(ReportUiState())
    val reportState = _reportState.asStateFlow()

    init {
        viewModelScope.launch {
            _subjectState.update {
                subjectRepository.getByGroupId(student.group.id).map {
                    Subject.toData(it)
                }
            }
        }
    }

    fun formReport(subject: Subject) {
        viewModelScope.launch {
            val lessons = lessonRepository.getBySubjectIdAndGroupIdAndStartTime(
                subject.id,
                student.group.id,
                TimeFormatter.getCurrentTime()
            ).map {
                Lesson.fromResponseToData(it)
            }

            val marks = markRepository.getByStudentIdAndSubjectId(
                student.id,
                subject.id
            ).map {
                Mark.toData(it)
            }
            val markedLessonIds = marks.map {
                it.lessonId
            }

            val reportLessons: MutableList<ReportLesson> = mutableListOf()
            lessons.forEach { lesson ->
                reportLessons += ReportLesson(lesson, markedLessonIds.contains(lesson.id))
            }

            val percentage = marks.count().toFloat() / lessons.count() * 100

            _reportState.update {
                ReportUiState(subject, student, reportLessons, marks, percentage)
            }
        }
    }
}