package com.maestrx.studentcontrol.teacherapp.spinner.subjects

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.maestrx.studentcontrol.teacherapp.R
import com.maestrx.studentcontrol.teacherapp.model.Subject

class SubjectsSpinnerAdapter(
    context: Context,
    subjects: List<Subject>
) : ArrayAdapter<Subject>(context, R.layout.spinner_subject, subjects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val maxLength = 12
        val view = super.getView(position, convertView, parent)
        val name = getItem(position)?.name ?: ""
        val viewName = if (name.length > maxLength) {
            name.substring(0..maxLength) + "..."
        } else {
            name
        }
        view.findViewById<TextView>(R.id.subjectText).text = viewName
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val subject = getItem(position)
        view.findViewById<TextView>(R.id.subjectText).text = subject?.name
        return view
    }
}