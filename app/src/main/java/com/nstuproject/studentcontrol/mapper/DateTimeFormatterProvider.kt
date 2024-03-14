package com.nstuproject.studentcontrol.mapper

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateTimeFormatterProvider @Inject constructor(
    private val zoneId: ZoneId,
) {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")

    fun format(instant: Instant): String = formatter.format(instant.atZone(zoneId))
}