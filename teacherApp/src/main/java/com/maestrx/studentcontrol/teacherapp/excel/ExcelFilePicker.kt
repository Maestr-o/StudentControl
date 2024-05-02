package com.maestrx.studentcontrol.teacherapp.excel

import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts

class ExcelFilePicker(
    activityResultRegistry: ActivityResultRegistry,
    callback: (uri: Uri?) -> Unit,
) {

    companion object {
        const val REGISTRY_KEY_GET_EXCEL = "REGISTRY_KEY_GET_EXCEL"
        const val EXCEL_MIME_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    }

    private val getContent = activityResultRegistry.register(
        REGISTRY_KEY_GET_EXCEL,
        ActivityResultContracts.GetContent(),
        callback
    )

    fun pickFile() {
        getContent.launch(EXCEL_MIME_TYPE)
    }
}