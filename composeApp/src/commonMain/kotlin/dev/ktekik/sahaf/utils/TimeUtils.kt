package dev.ktekik.sahaf.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

/**
 * Returns a human-readable relative time string based on the given instant.
 *
 * Rules:
 * - Within the same hour: "mm minutes ago"
 * - More than an hour but same day: "Today at hh:mm"
 * - Previous day (after 1 midnight): "Yesterday at hh:mm"
 * - More than 2 midnights ago: "on MM/dd"
 *
 * @param instant The point in time to format
 * @param now Current time (defaults to Clock.System.now() for easier testing)
 * @param timeZone TimeZone to use (defaults to system timezone)
 * @return Formatted relative time string
 */
fun getRelativeTimeString(
    instant: Instant,
    now: Instant = Clock.System.now(),
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val instantDateTime = instant.toLocalDateTime(timeZone)
    val nowDateTime = now.toLocalDateTime(timeZone)

    val instantDate =
        LocalDate(instantDateTime.year, instantDateTime.monthNumber, instantDateTime.dayOfMonth)
    val nowDate = LocalDate(nowDateTime.year, nowDateTime.monthNumber, nowDateTime.dayOfMonth)

    // Calculate the number of midnights between the two dates
    val midnightsPassed = countMidnightsBetween(instantDate, nowDate)

    return when (midnightsPassed) {
        0 -> {
            if (isWithinAnHour(instant, now)) {
                val minutesAgo =
                    ((now.toEpochMilliseconds() - instant.toEpochMilliseconds()) / 60_000).toInt()
                when (minutesAgo) {
                    0 -> "Just now"
                    1 -> "1 minute ago"
                    else -> "$minutesAgo minutes ago"
                }
            } else {
                val hour = instantDateTime.hour
                val minute = instantDateTime.minute
                "Today at ${formatTime(hour, minute)}"
            }
        }

        // Yesterday (1 midnight passed)
        1 -> {
            val hour = instantDateTime.hour
            val minute = instantDateTime.minute
            "Yesterday at ${formatTime(hour, minute)}"
        }

        // More than 2 midnights ago
        else -> {
            val month = instantDateTime.monthNumber
            val day = instantDateTime.dayOfMonth
            "on ${formatDate(month, day)}"
        }
    }
}

/**
 * Checks if two instants are within one hour of each other
 */
private fun isWithinAnHour(instant1: Instant, instant2: Instant): Boolean {
    val diffMillis = abs(instant2.toEpochMilliseconds() - instant1.toEpochMilliseconds())
    return diffMillis < 3_600_000 // 1 hour in milliseconds
}

/**
 * Counts the number of midnights between two dates
 * Returns 0 if same day, 1 if next day, 2 if day after, etc.
 */
private fun countMidnightsBetween(date1: LocalDate, date2: LocalDate): Int {
    // Get the number of days between dates
    val epochDay1 = date1.toEpochDays()
    val epochDay2 = date2.toEpochDays()
    return (epochDay2 - epochDay1).coerceAtLeast(0)
}

/**
 * Formats time in 12-hour format with AM/PM
 */
private fun formatTime(hour: Int, minute: Int): String {
    val period = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "${displayHour}:${minute.toString().padStart(2, '0')} $period"
}

/**
 * Formats date as MM/dd
 */
private fun formatDate(month: Int, day: Int): String {
    return "${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
}
