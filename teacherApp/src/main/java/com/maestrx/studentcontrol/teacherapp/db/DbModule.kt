package com.maestrx.studentcontrol.teacherapp.db

import android.app.Application
import androidx.room.Room
import com.maestrx.studentcontrol.teacherapp.db.dao.GroupDao
import com.maestrx.studentcontrol.teacherapp.db.dao.LessonDao
import com.maestrx.studentcontrol.teacherapp.db.dao.LessonGroupCrossDao
import com.maestrx.studentcontrol.teacherapp.db.dao.MarkDao
import com.maestrx.studentcontrol.teacherapp.db.dao.StudentDao
import com.maestrx.studentcontrol.teacherapp.db.dao.SubjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {

    @Singleton
    @Provides
    fun provideDb(app: Application): AppDb =
        Room.databaseBuilder(
            app,
            AppDb::class.java,
            "data.db",
        )
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideAttendanceDao(db: AppDb): MarkDao = db.markDao

    @Provides
    fun provideGroupDao(db: AppDb): GroupDao = db.groupDao

    @Provides
    fun provideLessonDao(db: AppDb): LessonDao = db.lessonDao

    @Provides
    fun provideLessonGroupCrossDao(db: AppDb): LessonGroupCrossDao = db.lessonGroupCrossDao

    @Provides
    fun provideStudentDao(db: AppDb): StudentDao = db.studentDao

    @Provides
    fun provideSubjectDao(db: AppDb): SubjectDao = db.subjectDao
}