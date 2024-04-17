package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

import com.maestrx.studentcontrol.studentapp.domain.model.Student

data class LoadingUiState(
    val screenState: LoadingStatus = LoadingStatus.Loading,
    val students: List<Student> = mutableListOf()
)