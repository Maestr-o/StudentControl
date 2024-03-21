package com.nstuproject.studentcontrol.viewmodel

import com.nstuproject.studentcontrol.model.Group

data class GroupsChooseUiState(
    val selectedPositions: List<Int> = emptyList(),
    val selectedGroups: List<Group> = emptyList(),
)