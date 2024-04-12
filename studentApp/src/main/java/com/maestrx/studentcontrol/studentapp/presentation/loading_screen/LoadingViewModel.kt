package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LoadingUiState())
    val state = _state.asStateFlow()
}