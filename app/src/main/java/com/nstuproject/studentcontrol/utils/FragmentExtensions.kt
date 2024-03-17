package com.nstuproject.studentcontrol.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nstuproject.studentcontrol.R

fun Fragment.toastBlankData() {
    Toast.makeText(
        requireContext(),
        getString(R.string.blank_content),
        Toast.LENGTH_SHORT
    ).show()
}

fun Fragment.toast(resId: Int) {
    Toast.makeText(
        requireContext(),
        getString(resId),
        Toast.LENGTH_SHORT
    ).show()
}