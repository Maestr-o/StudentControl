package com.maestrx.studentcontrol.studentapp.data

import android.content.Context
import androidx.core.content.edit
import com.maestrx.studentcontrol.studentapp.domain.model.PersonalData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val PREFS_PERSONAL_DATA = "personal_data"
        const val PERSONAL_DATA_GROUP = "PERSONAL_DATA_GROUP"
        const val PERSONAL_DATA_NAME = "PERSONAL_DATA_NAME"
    }

    private val prefs =
        context.getSharedPreferences(PREFS_PERSONAL_DATA, Context.MODE_PRIVATE)

    fun getPersonalData(): PersonalData? {
        return prefs?.run {
            PersonalData(
                group = getString(PERSONAL_DATA_GROUP, "") ?: "",
                fullName = getString(PERSONAL_DATA_NAME, "") ?: "",
            )
        }
    }

    fun savePersonalData(group: String, name: String): Boolean =
        try {
            prefs.edit {
                putString(PERSONAL_DATA_GROUP, group)
                putString(PERSONAL_DATA_NAME, name)
            }
            true
        } catch (_: Exception) {
            false
        }
}