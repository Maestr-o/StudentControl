package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

import com.maestrx.studentcontrol.studentapp.domain.model.PersonalData

data class LoadingUiState(
    val screenState: LoadingState = LoadingState.Input,
    val list: List<PersonalData> = mutableListOf()
)