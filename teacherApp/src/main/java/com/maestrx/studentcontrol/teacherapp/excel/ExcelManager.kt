package com.maestrx.studentcontrol.teacherapp.excel

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.SubjectEntity
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.LessonResponse
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.repository.attendance.AttendanceRepository
import com.maestrx.studentcontrol.teacherapp.repository.group.GroupRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.repository.subject.SubjectRepository
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.TimeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@ViewModelScoped
class ExcelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subjectRepository: SubjectRepository,
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository,
    private val groupRepository: GroupRepository,
    private val lessonRepository: LessonRepository,
) {

    suspend fun export(): Boolean = withContext(Dispatchers.IO) {
        val subjectsDeferred = async {
            subjectRepository.getAll().first()
        }
        val groupsDeferred = async {
            groupRepository.getAll().first()
        }
        val (subjects, groups) = awaitAll(subjectsDeferred, groupsDeferred)

        if (attendanceRepository.getCount() == 0) {
            return@withContext false
        }

        subjects.forEach { subjectEntity ->
            launch {
                val subject = Subject.toData(subjectEntity as SubjectEntity)
                createWorkbook(subject, groups.map { it as GroupEntity })
            }
        }
        return@withContext true
    }

    private suspend fun createWorkbook(subject: Subject, groups: List<GroupEntity>) {
        val workbook = XSSFWorkbook()
        val styles = ExcelStyles(workbook)

        var count = 0
        groups.forEach { groupEntity ->
            if (lessonRepository.getCountBySubjectIdAndGroupId(subject.id, groupEntity.id) >= 1
                && createSheet(workbook, styles, subject, Group.toData(groupEntity))
            ) {
                count++
            }
        }
        if (count > 0) {
            val fileName = subject.name + Constants.EXCEL_FORMAT
            writeFile(fileName, workbook)
        }
    }

    private suspend fun createSheet(
        workbook: XSSFWorkbook,
        styles: ExcelStyles,
        subject: Subject,
        group: Group
    ): Boolean = withContext(Dispatchers.Default) {
        val lessonsDeferred = async(Dispatchers.IO) {
            lessonRepository.getBySubjectIdAndGroupId(subject.id, group.id)
        }
        val studentsDeferred = async(Dispatchers.IO) {
            studentRepository.getByGroupId(group.id).first()
        }
        val attendancesDeferred = async(Dispatchers.IO) {
            attendanceRepository.getBySubjectIdAndGroupId(subject.id, group.id)
        }
        val (lessons, students, attendances) = awaitAll(
            lessonsDeferred, studentsDeferred, attendancesDeferred
        )

        if (attendances.isEmpty() || students.isEmpty() || lessons.isEmpty()) {
            return@withContext false
        }

        try {
            workbook.createSheet(group.name).apply {
                setColumnWidth(0, Constants.EXCEL_N_COLUMN_SIZE)
                setColumnWidth(1, Constants.EXCEL_NAME_COLUMN_SIZE)

                var x = 0
                createRow(x++).apply {
                    var y = 0
                    createCell(y++).apply {
                        setCellStyle(styles.headerText)
                        setCellValue(context.getString(R.string.quantity))
                    }
                    createCell(y++).apply {
                        setCellStyle(styles.headerText)
                        setCellValue(context.getString(R.string.person_name_header))
                    }
                    lessons.forEach { lessonResponse ->
                        val lesson =
                            Lesson.fromResponseToData(lessonResponse as LessonResponse)
                        createCell(y++).apply {
                            setCellStyle(styles.headerDate)
                            setCellValue(
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(lesson.timeStart),
                                    ZoneId.systemDefault()
                                )
                            )
                        }
                    }
                    createCell(y++).apply {
                        setCellStyle(styles.headerText)
                        setCellValue(context.getString(R.string.amount))
                    }
                    createCell(y++).apply {
                        setCellStyle(styles.headerText)
                        setCellValue(context.getString(R.string.percent))
                    }
                }

                val listOfPercentAttendance: MutableList<Double> = mutableListOf()
                students.forEachIndexed { index, studentResponse ->
                    val student =
                        Student.fromResponseToData(studentResponse as StudentResponse)
                    createRow(x++).apply {
                        var y = 0
                        var attendedCount = 0
                        createCell(y++).apply {
                            setCellStyle(styles.number)
                            setCellValue((index + 1).toDouble())
                        }
                        createCell(y++).apply {
                            setCellStyle(styles.textLeft)
                            setCellValue(student.fullName)
                        }
                        lessons.forEach { lessonResponse ->
                            val lesson =
                                Lesson.fromResponseToData(lessonResponse as LessonResponse)
                            val isAttended = attendances.any { attendance ->
                                (attendance as AttendanceEntity).lessonId == lesson.id &&
                                        student.id == attendance.studentId
                            }
                            if (isAttended) {
                                createCell(y++).apply {
                                    setCellStyle(styles.textCenter)
                                    setCellValue("+")
                                }
                                attendedCount++
                            } else {
                                createCell(y++).apply {
                                    setCellStyle(styles.textCenter)
                                    setCellValue("")
                                }
                            }
                        }
                        createCell(y++).apply {
                            setCellStyle(styles.decimal)
                            setCellValue(attendedCount.toDouble())
                        }

                        val percent = attendedCount.toDouble() / lessons.count()
                        listOfPercentAttendance += percent
                        createCell(y++).apply {
                            setCellStyle(styles.percent)
                            setCellValue(percent)
                        }
                    }
                }

                createRow(x++).apply {
                    var y = 1
                    createCell(y++).apply {
                        setCellStyle(styles.headerText)
                        setCellValue(context.getString(R.string.total))
                    }
                    lessons.forEach { lessonResponse ->
                        val lesson =
                            Lesson.fromResponseToData(lessonResponse as LessonResponse)
                        createCell(y++).apply {
                            setCellStyle(styles.headerDecimal)
                            setCellValue(
                                attendanceRepository.getCountByLessonIdAndGroupId(
                                    lesson.id,
                                    group.id
                                ).toDouble()
                            )
                        }
                    }
                    createCell(y++).apply {
                        setCellStyle(styles.headerDecimal)
                        setCellValue(lessons.count().toDouble())
                    }

                    var avgPercentAttendance = 0.0
                    listOfPercentAttendance.forEach {
                        avgPercentAttendance += it
                    }
                    avgPercentAttendance /= students.count()
                    createCell(y++).apply {
                        setCellStyle(styles.headerPercent)
                        setCellValue(avgPercentAttendance)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_TAG, "Exporting error: $e")
            return@withContext false
        }
        return@withContext true
    }

    private fun writeFile(fileName: String, workbook: XSSFWorkbook): Boolean =
        try {
            if (Build.VERSION.SDK_INT <= 28) {
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS,
                    ),
                    context.getString(
                        R.string.export_path_short,
                        TimeFormatter.getCurrentTimeString()
                    )
                )
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val file = File(dir, fileName)
                file.createNewFile()

                val fileOutputStream = FileOutputStream(file)
                workbook.write(fileOutputStream)
                fileOutputStream.close()
                workbook.close()
                Log.d(Constants.DEBUG_TAG, "File created in ${file.path}")
            } else {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        context.getString(
                            R.string.export_path_long,
                            Environment.DIRECTORY_DOWNLOADS,
                            TimeFormatter.getCurrentTimeString()
                        )
                    )
                }

                val dstUri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                if (dstUri != null) {
                    context.contentResolver.openOutputStream(dstUri).use { outputStream ->
                        workbook.write(outputStream)
                    }
                }
                Log.d(Constants.DEBUG_TAG, "File created in ${dstUri?.path}")
            }
            true
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_TAG, "Error write to file: $e")
            false
        }
}