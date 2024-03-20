package com.nstuproject.studentcontrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentLessonsBinding
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.recyclerview.lessons.LessonAdapter
import com.nstuproject.studentcontrol.utils.toast
import com.nstuproject.studentcontrol.viewmodel.LessonsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LessonsFragment : Fragment() {

    private val viewModel by viewModels<LessonsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLessonsBinding.inflate(layoutInflater)

        val adapter = LessonAdapter(
            object : LessonAdapter.LessonsListener {
                override fun onClickListener(lesson: Lesson) {
                    // send lesson
                    requireParentFragment().requireParentFragment().findNavController()
                        .navigate(R.id.action_bottomNavigationFragment_to_lessonDetailsFragment)
                }
            }
        )
        binding.lessons.adapter = adapter

        viewModel.state.onEach { state ->
            adapter.submitList(state)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })

        return binding.root
    }

    fun addLesson() {
        if (viewModel.groupsCount.value == 0L) {
            toast(R.string.zero_groups_error)
        } else if (viewModel.subjectsCount.value == 0L) {
            toast(R.string.zero_subjects_error)
        } else {
            requireParentFragment().requireParentFragment().findNavController()
                .navigate(R.id.action_bottomNavigationFragment_to_newLessonFragment)
        }
    }
}