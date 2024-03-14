package com.nstuproject.studentcontrol.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->

        }

        val calendar = Calendar.getInstance()
        val initialHours = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinutes = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHours,
            initialMinutes,
            true
        )
    }
}