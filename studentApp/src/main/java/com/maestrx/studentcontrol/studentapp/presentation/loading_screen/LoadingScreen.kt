package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maestrx.studentcontrol.studentapp.R
import com.maestrx.studentcontrol.studentapp.data.SharedPreferencesManager
import com.maestrx.studentcontrol.studentapp.domain.model.Group
import com.maestrx.studentcontrol.studentapp.domain.model.Student
import com.maestrx.studentcontrol.studentapp.util.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoadingScreen(
    appContext: Context,
    state: LoadingUiState,
    onEvent: (LoadingEvent) -> Unit,
    prefs: SharedPreferencesManager,
    isLocationEnabled: Boolean,
    isWifiEnabled: Boolean,
    badState: () -> Unit,
    navClick: (isConnected: Boolean) -> Unit,
) {
    val context = LocalContext.current

    if (!isLocationEnabled || !isWifiEnabled) {
        badState()
    }

    when (state.screenState) {
        is LoadingStatus.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 10.dp),
                        text = stringResource(id = R.string.sending_data),
                        fontSize = 20.sp,
                    )
                    CircularProgressIndicator()
                }
                Button(
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        navClick(false)
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
            LaunchedEffect(Unit) {
                onEvent(LoadingEvent.StartDataExchange)
            }
        }

        is LoadingStatus.Input -> {
            val savedData = prefs.getPersonalData()
            if (savedData != null && savedData.group.isNotBlank() && savedData.fullName.isNotBlank()) {
                val check = state.students.filter { student ->
                    student.group.name == savedData.group && student.fullName == savedData.fullName
                }
                if (check.isNotEmpty()) {
                    onEvent(LoadingEvent.SetStudentId(check[0].id))
                    return
                }
            }
            var groups = mutableListOf<Group>()
            state.students.forEach {
                groups += it.group
            }
            groups = groups.distinct().sortedBy { group ->
                group.name
            }.toMutableList()

            var isExpandedGroups by rememberSaveable { mutableStateOf(false) }
            var isExpandedStudents by rememberSaveable { mutableStateOf(false) }
            var selectedGroup by rememberSaveable { mutableStateOf(groups[0]) }
            var selectedStudent by rememberSaveable { mutableStateOf<Student?>(null) }

            val students = state.students.filter { student ->
                student.group.id == selectedGroup.id
            }.sortedBy { student ->
                student.lastName
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        text = stringResource(id = R.string.choose_group),
                        fontSize = 18.sp,
                    )
                    ExposedDropdownMenuBox(
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        expanded = isExpandedGroups,
                        onExpandedChange = {
                            isExpandedGroups = !isExpandedGroups
                        }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .padding(bottom = 8.dp),
                            value = selectedGroup.name,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedGroups)
                            },
                        )

                        ExposedDropdownMenu(
                            expanded = isExpandedGroups,
                            onDismissRequest = { isExpandedGroups = false }
                        ) {
                            groups.forEachIndexed { index, group ->
                                DropdownMenuItem(
                                    text = { Text(text = group.name) },
                                    onClick = {
                                        selectedGroup = groups[index]
                                        isExpandedGroups = false
                                        selectedStudent = null
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        text = stringResource(id = R.string.choose_student),
                        fontSize = 18.sp,
                    )
                    ExposedDropdownMenuBox(
                        expanded = isExpandedStudents,
                        onExpandedChange = {
                            isExpandedStudents = !isExpandedStudents
                        }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            value = selectedStudent?.fullName ?: "",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedStudents)
                            },
                        )

                        ExposedDropdownMenu(
                            expanded = isExpandedStudents,
                            onDismissRequest = { isExpandedStudents = false }
                        ) {
                            students.forEachIndexed { index, student ->
                                DropdownMenuItem(
                                    text = { Text(text = student.fullName) },
                                    onClick = {
                                        selectedStudent = students[index]
                                        isExpandedStudents = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        text = stringResource(id = R.string.choose_student_hint),
                        fontSize = 16.sp,
                    )
                }

                Button(
                    onClick = {
                        selectedStudent?.id?.let { id ->
                            onEvent(LoadingEvent.SetStudentId(id))
                            selectedStudent?.fullName?.let { name ->
                                prefs.savePersonalData(selectedGroup.name, name)
                            }
                        }
                    },
                    enabled = selectedStudent != null,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(text = stringResource(id = R.string.send))
                }
            }
        }

        is LoadingStatus.Error -> {
            context.Toast(res = R.string.error_exchanging_data)
            onEvent(LoadingEvent.SetScreenStatus(LoadingStatus.ReadyToBack(false)))
        }

        is LoadingStatus.Success -> {
            onEvent(LoadingEvent.SetScreenStatus(LoadingStatus.ReadyToBack(true)))
        }

        is LoadingStatus.ReadyToBack -> {
            navClick(state.screenState.isConnected)
        }
    }
}