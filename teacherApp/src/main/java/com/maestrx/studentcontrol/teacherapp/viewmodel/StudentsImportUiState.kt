package com.maestrx.studentcontrol.teacherapp.viewmodel

data class StudentsImportUiState(
    val fileName: String = "",
    val tableNames: List<String> = emptyList(),
    val selectedTable: String = "",
    val column: String = "",
    val startX: String = "",
    val endX: String = "",
)