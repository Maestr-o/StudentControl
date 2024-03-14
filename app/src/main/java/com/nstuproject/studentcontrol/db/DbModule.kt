package com.nstuproject.studentcontrol.db

import android.app.Application
import androidx.room.Room
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
}