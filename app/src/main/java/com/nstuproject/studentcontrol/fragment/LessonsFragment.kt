package com.nstuproject.studentcontrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nstuproject.studentcontrol.databinding.FragmentLessonsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LessonsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLessonsBinding.inflate(layoutInflater)

        return binding.root
    }
}