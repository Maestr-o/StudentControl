package com.nstuproject.studentcontrol.utils

import android.icu.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeFormatter {
    fun unixTimeToDateString(milliseconds: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val date =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault())
        return formatter.format(date)
    }

    fun unixTimeToTimeString(milliseconds: Long): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val time =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault())
        return formatter.format(time)
    }

    fun stringToUnixTime(dateString: String, timeString: String): Long {
        val dateTimeString = "$dateString $timeString"
        val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val dateTime = dateTimeFormat.parse(dateTimeString)
        return dateTime?.time ?: 0L
    }
}