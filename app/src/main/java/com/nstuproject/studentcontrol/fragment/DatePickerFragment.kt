package com.nstuproject.studentcontrol.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val resultDate = GregorianCalendar(year, month, dayOfMonth).time
//            if (toolbarViewModel.getCurrentFragmentKey() == GlobalObject.EDIT_CRIME_KEY) {
//                editCrimeViewModel.setDate(resultDate)
//            } else if (toolbarViewModel.getCurrentFragmentKey() == GlobalObject.NEW_CRIME_KEY) {
//                newCrimeViewModel.setDate(resultDate)
//            }
//            toolbarViewModel.setCurrentFragmentKey(GlobalObject.DEFAULT_KEY)
        }

        val calendar = Calendar.getInstance()
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }
}