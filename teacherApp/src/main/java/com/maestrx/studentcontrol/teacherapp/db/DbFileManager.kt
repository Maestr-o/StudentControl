package com.maestrx.studentcontrol.teacherapp.db

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.TimeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbFileManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDb,
) {

    fun export() {
        val dbFile = context.getDatabasePath(Constants.DB_INT_NAME)
        checkpoint()
        if (Build.VERSION.SDK_INT <= 28) {
            getBckFile().run {
                dbFile.copyTo(this, true)
            }
        } else {
            getOutputStream()?.let { outs ->
                FileInputStream(dbFile).use { ins ->
                    ins.copyTo(outs)
                }
                outs.close()
            }
        }
    }

    fun import() {

    }

    private fun getBckFile(): File {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS,
            ),
            context.getString(R.string.export_path_db_short)
        )
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val fileName =
            context.getString(R.string.export_db_filename, TimeFormatter.getCurrentTimeString())
        val file = File(dir, fileName)
        file.createNewFile()
        Log.d(Constants.DEBUG_TAG, "File created in ${file.path}")
        return file
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getOutputStream(): OutputStream? {
        val contentValues = ContentValues().apply {
            val fileName =
                context.getString(R.string.export_db_filename, TimeFormatter.getCurrentTimeString())
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                context.getString(
                    R.string.export_path_long,
                    Environment.DIRECTORY_DOWNLOADS,
                    ""
                )
            )
        }

        val dstUri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )

        if (dstUri != null) {
            Log.d(Constants.DEBUG_TAG, "File created in ${dstUri.path}")
            return context.contentResolver.openOutputStream(dstUri)
        }
        return null
    }

    private fun checkpoint() {
        db.query("PRAGMA wal_checkpoint(FULL);", null)
        db.query("PRAGMA wal_checkpoint(TRUNCATE);", null)
    }
}