package com.nstuproject.studentcontrol.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentToolbarBinding
import com.nstuproject.studentcontrol.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ToolbarFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentFragmentManager.beginTransaction()
            .setPrimaryNavigationFragment(this)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentToolbarBinding.inflate(inflater)

        val navController =
            requireNotNull(childFragmentManager.findFragmentById(R.id.container)).findNavController()
        binding.toolbar.setupWithNavController(navController)

        val toolbarViewModel by activityViewModels<ToolbarViewModel>()

        val settings = binding.toolbar.menu.findItem(R.id.settings)
        val delete = binding.toolbar.menu.findItem(R.id.delete)
        val save = binding.toolbar.menu.findItem(R.id.save)

        toolbarViewModel.showSettings
            .onEach {
                settings.isVisible = it
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        toolbarViewModel.title
            .onEach { state ->
                if (state.isNotBlank()) {
                    binding.toolbar.title = state
                    toolbarViewModel.setTitle("")
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}