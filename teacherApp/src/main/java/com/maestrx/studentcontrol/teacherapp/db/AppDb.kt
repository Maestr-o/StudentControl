package com.maestrx.studentcontrol.teacherapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maestrx.studentcontrol.teacherapp.db.dao.AttendanceDao
import com.maestrx.studentcontrol.teacherapp.db.dao.GroupDao
import com.maestrx.studentcontrol.teacherapp.db.dao.LessonDao
import com.maestrx.studentcontrol.teacherapp.db.dao.LessonGroupCrossDao
import com.maestrx.studentcontrol.teacherapp.db.dao.StudentDao
import com.maestrx.studentcontrol.teacherapp.db.dao.SubjectDao
import com.maestrx.studentcontrol.teacherapp.db.entity.AttendanceEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.GroupEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.LessonEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.LessonGroupCrossEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.StudentEntity
import com.maestrx.studentcontrol.teacherapp.db.entity.SubjectEntity

@Database(
    version = 1,
    entities = [AttendanceEntity::class, GroupEntity::class, LessonEntity::class,
        LessonGroupCrossEntity::class, StudentEntity::class, SubjectEntity::class],
)
abstract class AppDb : RoomDatabase() {
    abstract val studentDao: StudentDao
    abstract val groupDao: GroupDao
    abstract val subjectDao: SubjectDao
    abstract val lessonDao: LessonDao
    abstract val attendanceDao: AttendanceDao
    abstract val lessonGroupCrossDao: LessonGroupCrossDao
}