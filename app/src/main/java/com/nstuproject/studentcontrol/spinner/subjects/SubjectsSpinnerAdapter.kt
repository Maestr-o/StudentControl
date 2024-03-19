package com.nstuproject.studentcontrol.spinner.subjects

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.nstuproject.studentcontrol.R
import com.nstuproject.studentcontrol.model.Subject

class SubjectsSpinnerAdapter(
    context: Context,
    private val subjects: List<Subject>
) : ArrayAdapter<Subject>(context, R.layout.spinner_subject, subjects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val subject = getItem(position)
        view.findViewById<TextView>(R.id.subjectText).text = subject?.name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val subject = getItem(position)
        view.findViewById<TextView>(R.id.subjectText).text = subject?.name
        return view
    }
}