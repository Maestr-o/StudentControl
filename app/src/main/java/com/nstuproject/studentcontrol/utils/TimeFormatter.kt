package com.nstuproject.studentcontrol.utils

import android.icu.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object TimeFormatter {
    fun unixTimeToDateStringWithDayOfWeek(milliseconds: Long): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")
        val date =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault())
        return formatter.format(date)
    }

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

    fun stringToUnixTime(timeString: String): Long {
        val dateTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateTime = dateTimeFormat.parse(timeString)
        return dateTime?.time ?: 0L
    }

    fun getCurrentDateZeroTime(): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
            .timeInMillis

    fun getEndTime(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
            .timeInMillis

    fun incDate(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.DAY_OF_MONTH, 1)
        }
            .timeInMillis

    fun decDate(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.DAY_OF_MONTH, -1)
        }
            .timeInMillis

    fun addDefaultLessonDuration(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.HOUR_OF_DAY, 1)
            add(Calendar.MINUTE, 30)
        }
            .timeInMillis
}