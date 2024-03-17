package com.nstuproject.studentcontrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nstuproject.studentcontrol.databinding.FragmentEditLessonBinding
import com.nstuproject.studentcontrol.viewmodel.NewLessonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class NewLessonFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditLessonBinding.inflate(inflater, container, false)

        val viewModel by viewModels<NewLessonViewModel>()

        viewModel.state.onEach {

        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}