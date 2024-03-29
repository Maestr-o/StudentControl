package com.nstuproject.studentcontrol.viewmodel.di

import com.nstuproject.studentcontrol.viewmodel.SettingsViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface SettingsViewModelFactory {
    fun create(state: String): SettingsViewModel
}