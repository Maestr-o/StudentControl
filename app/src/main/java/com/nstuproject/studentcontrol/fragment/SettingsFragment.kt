package com.nstuproject.studentcontrol.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentSettingsBinding
import com.nstuproject.studentcontrol.utils.Constants
import com.nstuproject.studentcontrol.utils.toast
import com.nstuproject.studentcontrol.viewmodel.SettingsViewModel
import com.nstuproject.studentcontrol.viewmodel.di.SettingsViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(layoutInflater)

        val prefs = context?.applicationContext?.getSharedPreferences(
            Constants.AP_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val apSSID = prefs?.getString(Constants.AP_SSID, "") ?: ""

        val viewModel by viewModels<SettingsViewModel>(
            extrasProducer = {
                defaultViewModelCreationExtras.withCreationCallback<SettingsViewModelFactory> { factory ->
                    factory.create(apSSID)
                }
            }
        )

        binding.saveData.setOnClickListener {
            val ssid = binding.ssid.text.toString().trim()
            if (ssid.isBlank()) {
                toast(R.string.empty_ssid)
            } else {
                viewModel.setState(ssid)
                prefs?.edit {
                    putString(Constants.AP_SSID, ssid)
                }
                toast(R.string.saved_data)
            }
        }

        viewModel.ssidState
            .onEach { state ->
                binding.apply {
                    ssid.setText(state)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}