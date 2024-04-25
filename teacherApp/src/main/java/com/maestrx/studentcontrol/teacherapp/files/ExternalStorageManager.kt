package com.maestrx.studentcontrol.teacherapp.files

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
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
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@ViewModelScoped
class ExternalStorageManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subjectRepository: SubjectRepository,
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository,
    private val groupRepository: GroupRepository,
    private val lessonRepository: LessonRepository,
) {

    suspend fun createWorkbook(): Boolean {
        val workbook = HSSFWorkbook()

        val subjects = subjectRepository.getAll().first()
        val groups = groupRepository.getAll().first()

        // TODO если у предмета нет групп - не создавать лист
        subjects.forEach { subjectEntity ->
            groups.forEach { groupEntity ->
                createSheet(workbook, Subject.toData(subjectEntity), Group.toData(groupEntity))
            }
        }

        val fileName =
            Constants.EXPORT_PREFIX + TimeFormatter.getCurrentTimeString() + Constants.EXCEL_FORMAT
        return writeFile(fileName, workbook)
    }

    private suspend fun createSheet(workbook: HSSFWorkbook, subject: Subject, group: Group) =
        withContext(Dispatchers.IO) {
            val lessonsDeferred = async {
                lessonRepository.getLessonsBySubjectAndGroup(subject.id, group.id)
            }
            val studentsDeferred = async {
                studentRepository.getStudentsByGroup(group.id).first()
            }
            val attendancesDeferred = async {
                attendanceRepository.getBySubjectAndGroup(subject.id, group.id)
            }
            val (lessons, students, attendances) = awaitAll(
                lessonsDeferred, studentsDeferred, attendancesDeferred
            )

            withContext(Dispatchers.Default) {
                workbook.createSheet("${subject.name} - ${group.name}").apply {
                    var x = 0
                    createRow(x++).apply {
                        var y = 0
                        createCell(y++).setCellValue(context.getString(R.string.quantity))
                        createCell(y++).setCellValue(context.getString(R.string.person_name_header))
                        lessons.forEach { lessonResponse ->
                            val lesson =
                                Lesson.fromResponseToData(lessonResponse as LessonResponse)
                            createCell(y++).setCellValue(
                                TimeFormatter.unixTimeToDateString(
                                    lesson.timeStart
                                )
                            )
                        }
                        createCell(y++).setCellValue(context.getString(R.string.amount))
                        createCell(y).setCellValue(context.getString(R.string.percent))
                    }

                    students.forEachIndexed { index, studentResponse ->
                        val student =
                            Student.fromResponseToData(studentResponse as StudentResponse)
                        createRow(x++).apply {
                            var y = 0
                            var count = 0
                            createCell(y++).setCellValue((index + 1).toString())
                            createCell(y++).setCellValue(student.fullName)
                            lessons.forEach { lessonResponse ->
                                val lesson =
                                    Lesson.fromResponseToData(lessonResponse as LessonResponse)
                                val isAttended = attendances.any { attendance ->
                                    (attendance as AttendanceEntity).lessonId == lesson.id &&
                                            student.id == attendance.studentId
                                }
                                if (isAttended) {
                                    createCell(y++).setCellValue("+")
                                    count++
                                } else {
                                    createCell(y++).setCellValue("")
                                }
                            }
                            createCell(y++).setCellValue(count.toString())
                            createCell(y++).setCellValue(
                                (count / lessons.count() * 100).toString()
                            )
                        }
                    }
                }
            }
        }

    private fun writeFile(fileName: String, workbook: HSSFWorkbook): Boolean =
        try {
            if (Build.VERSION.SDK_INT <= 28) {
                val dir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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
                }
                val dstUri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                if (dstUri != null) {
                    context.contentResolver.openOutputStream(dstUri).run {
                        workbook.write(this)
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