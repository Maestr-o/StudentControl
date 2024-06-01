package com.maestrx.studentcontrol.teacherapp.excel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.MarkEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.StudentEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.SubjectEntity
import com.maestrx.studentcontrol.teacherapp.model.Group
import com.maestrx.studentcontrol.teacherapp.model.Lesson
import com.maestrx.studentcontrol.teacherapp.model.LessonResponse
import com.maestrx.studentcontrol.teacherapp.model.Student
import com.maestrx.studentcontrol.teacherapp.model.StudentResponse
import com.maestrx.studentcontrol.teacherapp.model.Subject
import com.maestrx.studentcontrol.teacherapp.repository.group.GroupRepository
import com.maestrx.studentcontrol.teacherapp.repository.lesson.LessonRepository
import com.maestrx.studentcontrol.teacherapp.repository.mark.MarkRepository
import com.maestrx.studentcontrol.teacherapp.repository.student.StudentRepository
import com.maestrx.studentcontrol.teacherapp.repository.subject.SubjectRepository
import com.maestrx.studentcontrol.teacherapp.util.Constants
import com.maestrx.studentcontrol.teacherapp.util.DatePreferenceManager
import com.maestrx.studentcontrol.teacherapp.util.TimeFormatter
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
import javax.inject.Inject

@ViewModelScoped
class ExcelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subjectRepository: SubjectRepository,
    private val markRepository: MarkRepository,
    private val studentRepository: StudentRepository,
    private val groupRepository: GroupRepository,
    private val lessonRepository: LessonRepository,
    private val dateManager: DatePreferenceManager,
) {

    private var importWorkbook = XSSFWorkbook()

    suspend fun export(): Boolean = withContext(Dispatchers.IO) {
        val subjectsDeferred = async {
            subjectRepository.getAll().first()
        }
        val groupsDeferred = async {
            groupRepository.getAll().first()
        }
        val (subjects, groups) = awaitAll(subjectsDeferred, groupsDeferred)

        if (markRepository.getCount() == 0) {
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
        val semesterStart = dateManager.getDate()

        var count = 0
        groups.forEach { groupEntity ->
            if (lessonRepository.getCountBySubjectIdAndGroupIdAndStartEndTime(
                    subject.id,
                    groupEntity.id,
                    semesterStart,
                    TimeFormatter.getCurrentTimeAddRecess()
                ) >= 1
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
        val semesterStart = dateManager.getDate()

        val lessonsDeferred = async(Dispatchers.IO) {
            lessonRepository.getBySubjectIdAndGroupIdAndStartEndTime(
                subject.id,
                group.id,
                semesterStart,
                TimeFormatter.getCurrentTimeAddRecess()
            )
        }
        val studentsDeferred = async(Dispatchers.IO) {
            studentRepository.getByGroupId(group.id).first()
        }
        val marksDeferred = async(Dispatchers.IO) {
            markRepository.getBySubjectIdAndGroupIdAndStartTime(subject.id, group.id, semesterStart)
        }
        val (lessons, students, marks) = awaitAll(
            lessonsDeferred, studentsDeferred, marksDeferred
        )

        if (marks.isEmpty() || students.isEmpty() || lessons.isEmpty()) {
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
                            val str = "${
                                TimeFormatter.getWeekNumberFromDate(
                                    dateManager.getDate(),
                                    lesson.timeStart
                                )
                            } ${context.getString(R.string.week)}\n${
                                TimeFormatter.unixTimeToShortDateString(lesson.timeStart)
                            }"
                            setCellStyle(styles.headerText)
                            setCellValue(str)
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

                val listOfPercentMark: MutableList<Double> = mutableListOf()
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
                            val isAttended = marks.any { mark ->
                                (mark as MarkEntity).lessonId == lesson.id &&
                                        student.id == mark.studentId
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
                        listOfPercentMark += percent
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
                                markRepository.getCountByLessonIdAndGroupId(
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

                    var avgPercentMark = 0.0
                    listOfPercentMark.forEach {
                        avgPercentMark += it
                    }
                    avgPercentMark /= students.count()
                    createCell(y++).apply {
                        setCellStyle(styles.headerPercent)
                        setCellValue(avgPercentMark)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_TAG, "Exporting error: ${e.printStackTrace()}")
            return@withContext false
        }
        return@withContext true
    }

    private fun writeFile(fileName: String, workbook: XSSFWorkbook): Boolean =
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
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
            Log.d(Constants.DEBUG_TAG, "Error write to file: ${e.printStackTrace()}")
            false
        }

    suspend fun importStudents(
        uri: Uri,
        sheetName: String,
        groupId: Long,
        column: String,
        startX: Int,
    ): List<StudentEntity>? = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
        val sheet = importWorkbook.getSheet(sheetName)
        val students: MutableList<StudentEntity> = mutableListOf()

        withContext(Dispatchers.Default) {
            val columnNum = columnToIndex(column)
            var index = startX - 1
            var emptyCellFlag = false
            while (!emptyCellFlag) {
                try {
                    val fullName = sheet.getRow(index).getCell(columnNum).stringCellValue
                    index += 1
                    val names = fullName.split(" ")

                    val lastName = names[0].ifBlank {
                        emptyCellFlag = true
                        throw IllegalArgumentException("Empty string")
                    }
                    val firstName = if (names.size > 1) {
                        names[1]
                    } else {
                        throw IllegalArgumentException("Incorrect string")
                    }
                    val midName = if (names.size > 2) {
                        names[2]
                    } else {
                        ""
                    }

                    students += StudentEntity(
                        groupId = groupId,
                        lastName = lastName,
                        firstName = firstName,
                        midName = midName,
                    )
                } catch (e: NullPointerException) {
                    Log.d(Constants.DEBUG_TAG, e.message.toString())
                    emptyCellFlag = true
                } catch (e: Exception) {
                    Log.d(Constants.DEBUG_TAG, e.message.toString())
                }
            }
        }
        inputStream.close()
        return@withContext students
    }

    suspend fun getExcelTableNames(fileUri: Uri): List<String> = withContext(Dispatchers.Default) {
        val names = mutableListOf<String>()
        context.contentResolver.openInputStream(fileUri).use { inputStream ->
            importWorkbook = XSSFWorkbook(inputStream)
            for (i in 0 until importWorkbook.numberOfSheets) {
                names.add(importWorkbook.getSheetName(i))
            }
        }
        return@withContext names
    }

    private fun columnToIndex(column: String): Int {
        var number = 0
        var power = 1
        for (i in column.length - 1 downTo 0) {
            val char = column[i]
            val value = char - 'A' + 1
            number += value * power
            power *= 26
        }
        return number - 1
    }
}