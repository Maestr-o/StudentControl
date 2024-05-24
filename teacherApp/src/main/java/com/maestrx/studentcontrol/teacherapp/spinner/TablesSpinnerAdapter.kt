package com.maestrx.studentcontrol.teacherapp.spinner

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.maestrx.studentcontrol.teacherapp.R

class TablesSpinnerAdapter(
    context: Context,
    subjects: List<String>
) : ArrayAdapter<String>(context, R.layout.spinner_item, subjects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val name = getItem(position) ?: ""
        view.findViewById<TextView>(R.id.textItem).text = name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val table = getItem(position)
        view.findViewById<TextView>(R.id.textItem).text = table
        return view
    }
}