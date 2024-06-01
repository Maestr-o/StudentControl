package com.maestrx.studentcontrol.teacherapp.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatePreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val PREFERENCES_FILE_NAME = "date_preferences"
        private const val DATE_KEY = "date_key"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    fun saveDate(dateInMillis: Long) {
        with(sharedPreferences.edit()) {
            putLong(DATE_KEY, dateInMillis)
            apply()
        }
    }

    fun getDate(): Long {
        return sharedPreferences.getLong(DATE_KEY, TimeFormatter.getUnixTimeForFirstSeptember())
    }

    fun doesPreferencesExist(): Boolean {
        val defPath = context.filesDir.parent
        val prefsFile = if (defPath != null) {
            File("$defPath/shared_prefs/$PREFERENCES_FILE_NAME.xml")
        } else {
            null
        }
        return prefsFile?.exists() ?: false
    }
}