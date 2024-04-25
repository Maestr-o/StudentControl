package com.maestrx.studentcontrol.teacherapp.files

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.maestrx.studentcontrol.teacherapp.utils.Constants
import com.maestrx.studentcontrol.teacherapp.utils.TimeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExternalStorageManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun createWorkbook(): Boolean {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Sheet 1")
        val row = sheet.createRow(0)

        val testData = listOf("1", "2", "3", "4", "5")

        testData.forEachIndexed { index, str ->
            val cell = row.createCell(index)
            cell.setCellValue(str)
        }

        val fileName =
            Constants.EXPORT_PREFIX + TimeFormatter.getCurrentTimeString() + Constants.EXCEL_FORMAT
        return writeFile(fileName, workbook)
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
            }
            true
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_TAG, "Error write to file: $e")
            false
        }
}