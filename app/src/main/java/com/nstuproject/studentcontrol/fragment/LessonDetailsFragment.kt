package com.nstuproject.studentcontrol.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.databinding.FragmentLessonDetailsBinding
import com.nstuproject.studentcontrol.model.Lesson
import com.nstuproject.studentcontrol.model.LessonType
import com.nstuproject.studentcontrol.recyclerview.groupsSelected.GroupSelectedAdapter
import com.nstuproject.studentcontrol.utils.Constants
import com.nstuproject.studentcontrol.viewmodel.LessonDetailsViewModel
import com.nstuproject.studentcontrol.viewmodel.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class LessonDetailsFragment : Fragment() {

    private val toolbarViewModel by activityViewModels<ToolbarViewModel>()

    override fun onStart() {
        super.onStart()
        toolbarViewModel.showDelete(true)
        toolbarViewModel.showEdit(true)
    }

    override fun onStop() {
        super.onStop()
        toolbarViewModel.showDelete(false)
        toolbarViewModel.showEdit(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLessonDetailsBinding.inflate(inflater, container, false)

        val viewModel by viewModels<LessonDetailsViewModel>()

        arguments?.let {
            val lesson = Json.decodeFromString<Lesson>(
                it.getString(Constants.LESSON_DATA) ?: Lesson().toString()
            )
            viewModel.setLesson(lesson)
        }

        val groupsAdapter = GroupSelectedAdapter()
        binding.groups.adapter = groupsAdapter

        toolbarViewModel.editClicked
            .onEach {
                if (toolbarViewModel.editClicked.value) {
                    val bundle = Bundle().apply {
                        putString(
                            Constants.LESSON_DATA,
                            Json.encodeToString(viewModel.lessonState.value)
                        )
                    }
                    findNavController().navigate(
                        R.id.action_lessonDetailsFragment_to_editLessonFragment,
                        bundle
                    )
                    toolbarViewModel.editClicked(false)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        toolbarViewModel.deleteClicked
            .onEach {
                if (toolbarViewModel.deleteClicked.value) {
                    AlertDialog.Builder(context)
                        .setTitle(R.string.ask_delete_lesson)
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            viewModel.deleteLesson()
                            requireActivity().supportFragmentManager.setFragmentResult(
                                Constants.LESSON_UPDATED,
                                bundleOf()
                            )
                            findNavController().navigateUp()
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    toolbarViewModel.deleteClicked(false)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.lessonState
            .onEach { state ->
                groupsAdapter.submitList(state.groups)
                binding.apply {
                    subject.text = state.subject.name
                    datetime.text = getString(
                        R.string.lesson_details_datetime,
                        state.date,
                        state.timeStart,
                        state.timeEnd
                    )
                    auditory.text = state.auditory
                    type.text = when (state.type.name) {
                        LessonType.LECTURE.toString() -> {
                            root.context.getString(R.string.lecture)
                        }

                        LessonType.PRACTICE.toString() -> {
                            root.context.getString(R.string.practice)
                        }

                        LessonType.LAB.toString() -> {
                            root.context.getString(R.string.laboratory_work)
                        }

                        else -> {
                            root.context.getString(R.string.lesson)
                        }
                    }
                    title.text = state.title
                    if (state.description.isNotBlank()) {
                        notesLayout.isVisible = true
                        notes.text = state.description
                    } else {
                        notesLayout.isGone = true
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}