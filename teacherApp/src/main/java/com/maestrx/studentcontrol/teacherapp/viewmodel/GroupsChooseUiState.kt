package com.maestrx.studentcontrol.teacherapp.viewmodel

import com.maestrx.studentcontrol.teacherapp.model.Group

data class GroupsChooseUiState(
    val selectedPositions: List<Int> = emptyList(),
    val selectedGroups: List<Group> = emptyList(),
)