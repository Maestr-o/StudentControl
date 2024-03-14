package com.nstuproject.studentcontrol.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nstuproject.studentcontrol.db.dao.GroupDao
import com.nstuproject.studentcontrol.db.dao.StudentDao
import com.nstuproject.studentcontrol.db.dao.SubjectDao
import com.nstuproject.studentcontrol.db.entity.AttendanceEntity
import com.nstuproject.studentcontrol.db.entity.GroupEntity
import com.nstuproject.studentcontrol.db.entity.LessonEntity
import com.nstuproject.studentcontrol.db.entity.LessonGroupCrossRefEntity
import com.nstuproject.studentcontrol.db.entity.StudentEntity
import com.nstuproject.studentcontrol.db.entity.SubjectEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Database(
    version = 1,
    entities = [AttendanceEntity::class, GroupEntity::class, LessonEntity::class,
        LessonGroupCrossRefEntity::class, StudentEntity::class, SubjectEntity::class],
)
abstract class AppDb : RoomDatabase() {
    abstract val studentDao: StudentDao
    abstract val groupDao: GroupDao
    abstract val subjectDao: SubjectDao
}