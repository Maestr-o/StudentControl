package com.maestrx.studentcontrol.teacherapp.utils

import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts

class FilePicker(
    activityResultRegistry: ActivityResultRegistry,
    callback: (uri: Uri?) -> Unit,
) {

    companion object {
        const val REGISTRY_KEY_GET_DB = "REGISTRY_KEY_GET_DB"
        const val REGISTRY_KEY_GET_EXCEL = "REGISTRY_KEY_GET_EXCEL"

        const val ALL_MIME_TYPE = "*/*"
        const val EXCEL_MIME_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    }

    private val getExcel = activityResultRegistry.register(
        REGISTRY_KEY_GET_EXCEL,
        ActivityResultContracts.GetContent(),
        callback
    )

    private val getDb = activityResultRegistry.register(
        REGISTRY_KEY_GET_DB,
        ActivityResultContracts.GetContent(),
        callback
    )

    fun pickExcelFile() {
        getExcel.launch(EXCEL_MIME_TYPE)
    }

    fun pickDbFile() {
        getDb.launch(ALL_MIME_TYPE)
    }
}