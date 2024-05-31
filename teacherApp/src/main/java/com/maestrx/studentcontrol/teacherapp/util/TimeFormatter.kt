package com.maestrx.studentcontrol.teacherapp.util

import android.icu.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object TimeFormatter {

    fun compareTimes(time1: String, time2: String): Int {
        val (hours1, minutes1) = time1.split(":").map { it.toInt() }
        val (hours2, minutes2) = time2.split(":").map { it.toInt() }

        if (hours1 == hours2) {
            return minutes1.compareTo(minutes2)
        }

        return hours1.compareTo(hours2)
    }

    fun unixTimeToDayOfWeek(milliseconds: Long): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE")
        val date =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault())
        return formatter.format(date)
    }

    fun unixTimeToDateString(milliseconds: Long?): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        return unixTimeToPatternString(formatter, milliseconds)
    }

    fun unixTimeToShortDateString(milliseconds: Long?): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM")
        return unixTimeToPatternString(formatter, milliseconds)
    }

    fun unixTimeToDateShortYearString(milliseconds: Long?): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
        return unixTimeToPatternString(formatter, milliseconds)
    }

    private fun unixTimeToDateTimeString(milliseconds: Long?): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH-mm")
        return unixTimeToPatternString(formatter, milliseconds)
    }

    private fun unixTimeToPatternString(formatter: DateTimeFormatter, milliseconds: Long?): String =
        if (milliseconds != null) {
            val date =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault())
            formatter.format(date)
        } else {
            val time = System.currentTimeMillis()
            val date =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
            formatter.format(date)
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

    fun stringDateToUnixTime(dateString: String): Long {
        val dateTimeString = "$dateString 00:00"
        val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateTime = dateTimeFormat.parse(dateTimeString)
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

    private fun getCurrentTime(): Long = Calendar.getInstance().timeInMillis

    fun getCurrentTimeAddRecess(): Long =
        Calendar.getInstance().apply {
            timeInMillis = getCurrentTime()
            add(Calendar.MINUTE, Constants.TIME_RECESS)
        }
            .timeInMillis

    fun getCurrentTimeString(): String =
        unixTimeToDateTimeString(Calendar.getInstance().timeInMillis)

    fun getEndTime(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
            .timeInMillis

    fun incDay(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.DAY_OF_MONTH, 1)
        }
            .timeInMillis

    fun decDay(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.DAY_OF_MONTH, -1)
        }
            .timeInMillis

    fun incHalfOfDay(time: Long) =
        Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.HOUR_OF_DAY, 12)
        }
            .timeInMillis

    fun addDefaultLessonDuration(time: Long): Long {
        val newTime = Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.HOUR_OF_DAY, 1)
            add(Calendar.MINUTE, 30)
        }
            .timeInMillis
        return if (unixTimeToDateString(newTime) != unixTimeToDateString(time)) {
            getEndTime(time)
        } else {
            newTime
        }
    }

    fun decRecess(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            add(Calendar.MINUTE, Constants.TIME_RECESS * -1)
        }
            .timeInMillis

    fun getDateZeroTime(time: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
            .timeInMillis

    fun getUnixTimeForFirstSeptember(): Long {
        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)

        val septemberFirstCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (currentCalendar.before(septemberFirstCalendar)) {
            septemberFirstCalendar.set(Calendar.YEAR, currentYear - 1)
        }

        return septemberFirstCalendar.timeInMillis
    }

    fun getWeekNumberFromDate(startDateInMillis: Long, currentDateInMillis: Long): Int {
        val startCalendar = Calendar.getInstance().apply {
            timeInMillis = startDateInMillis
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        val currentCalendar = Calendar.getInstance().apply {
            timeInMillis = currentDateInMillis
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }

        startCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val diffInMillis = currentCalendar.timeInMillis - startCalendar.timeInMillis
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        return diffInDays / 7 + 1
    }
}