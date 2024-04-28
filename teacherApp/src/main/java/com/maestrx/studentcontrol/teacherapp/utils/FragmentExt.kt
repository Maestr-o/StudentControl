package com.maestrx.studentcontrol.teacherapp.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.databinding.CustomToastLayoutBinding

fun Fragment.toastBlankData() {
    Toast.makeText(
        requireContext(),
        getString(R.string.blank_content),
        Toast.LENGTH_SHORT
    ).show()
}

fun Fragment.toast(resId: Int) {
    val binding = CustomToastLayoutBinding.inflate(layoutInflater)

    binding.toastText.text = getString(resId)

    with(Toast(requireContext())) {
        duration = Toast.LENGTH_LONG
        view = binding.root
        show()
    }
}

fun Fragment.showDatePicker(view: TextInputEditText) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(requireContext(), { _, y, m, dOfM ->
        val selectedCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, y)
            set(Calendar.MONTH, m)
            set(Calendar.DAY_OF_MONTH, dOfM)
        }
        val selectedDateInMillis = selectedCalendar.timeInMillis
        view.setText(TimeFormatter.unixTimeToDateString(selectedDateInMillis))
    }, year, month, dayOfMonth).apply {
        view.isEnabled = false
        setOnDismissListener {
            view.isEnabled = true
        }
        show()
    }
}

fun Fragment.showTimeStartPicker(startView: TextInputEditText, endView: TextInputEditText) {
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        requireContext(),
        { _, h, m ->
            val selectedTime = String.format("%02d:%02d", h, m)
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
            }
            startView.setText(selectedTime)
            endView.setText(
                TimeFormatter.unixTimeToTimeString(
                    TimeFormatter.addDefaultLessonDuration(calendar.timeInMillis)
                )
            )
        },
        hourOfDay,
        minute,
        true
    ).apply {
        startView.isEnabled = false
        setOnDismissListener {
            startView.isEnabled = true
        }
        show()
    }
}

fun Fragment.showTimeEndPicker(view: TextInputEditText, startTime: String) {
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        requireContext(),
        { _, h, m ->
            val selectedTime = String.format("%02d:%02d", h, m)
            if (TimeFormatter.compareTimes(startTime, selectedTime) >= 0) {
                toast(R.string.invalid_times)
            } else {
                view.setText(selectedTime)
            }
        },
        hourOfDay,
        minute,
        true
    ).apply {
        view.isEnabled = false
        setOnDismissListener {
            view.isEnabled = true
        }
        show()
    }
}