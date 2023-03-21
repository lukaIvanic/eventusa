package com.example.eventusa.extensions

import android.widget.TextView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val PATTERN_UI_DATE_TIME = "d MMM yyyy HH:mm"
const val PATTERN_UI_DATE = "EEE, d MMM yyyy"
const val PATTERN_UI_DATE_SHORT = "d MMM"
const val PATTERN_UI_TIME = "HH:mm"
const val PATTERN_SERVER = "yyyy-MM-dd'T'HH:mm:ss'Z'"

fun LocalDateTime.toParsedString(
    pattern: String = PATTERN_UI_DATE_TIME,
): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalDate.toParsedString(
    pattern: String = PATTERN_UI_DATE,
): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalTime.toParsedString(
    pattern: String = PATTERN_UI_TIME,
): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun String.toLocalDateTime(
    pattern: String = PATTERN_UI_DATE_TIME,
): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))
}

fun String.toLocalDate(
    pattern: String = PATTERN_UI_DATE,
): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))
}

fun String.plusTimeDays(days: Long): String = this.toLocalDate().plusDays(days).toParsedString()

fun String.toLocalTime(
    pattern: String = PATTERN_UI_TIME,
): LocalTime {
    return LocalTime.parse(this, DateTimeFormatter.ofPattern(pattern))
}

fun String.plusTimeHour(hours: Long = 1): String =
    this.toLocalTime().plusHours(hours).toParsedString()

fun String.plusTimeMinutes(minutes: Long = 1): String =
    this.toLocalTime().plusMinutes(minutes).toParsedString()

fun String.plusTime(minutes: Long = 1): String =
    this.toLocalTime().plusHours((minutes / 60)).plusMinutes(minutes % 60).toParsedString()





fun TextView.toLocalDate(): LocalDate = this.text.toString().toLocalDate()

fun TextView.plusTimeDays(days: Long): String = this.text.toString().plusTimeDays(days)

fun TextView.toLocalTime(): LocalTime = this.text.toString().toLocalTime()

fun TextView.plusTimeHour(hours: Long = 1): String = this.text.toString().plusTimeHour(hours)

fun TextView.plusTime(
    minutes: Long = 1,
): String {
    return this.text.toString().plusTime(minutes)
}