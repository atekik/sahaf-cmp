package dev.ktekik.sahaf.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeUtilsTest {

    private val timeZone = TimeZone.UTC

    // Helper to create an Instant from a LocalDateTime
    private fun createInstant(year: Int, month: Int, day: Int, hour: Int, minute: Int): Instant {
        return LocalDateTime(year, month, day, hour, minute, 0, 0).toInstant(timeZone)
    }

    @Test
    fun testJustNow() {
        val now = createInstant(2025, 1, 15, 10, 30)
        val instant = createInstant(2025, 1, 15, 10, 30) // Same time

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("Just now", result)
    }

    @Test
    fun testOneMinuteAgo() {
        val now = createInstant(2025, 1, 15, 10, 30)
        val instant = createInstant(2025, 1, 15, 10, 29) // 1 minute ago

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("1 minute ago", result)
    }

    @Test
    fun testMinutesAgo() {
        val now = createInstant(2025, 1, 15, 10, 30)
        val instant = createInstant(2025, 1, 15, 10, 15) // 15 minutes ago

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("15 minutes ago", result)
    }

    @Test
    fun testTodayAtTime() {
        val now = createInstant(2025, 1, 15, 14, 30)
        val instant = createInstant(2025, 1, 15, 9, 15) // Same day, more than an hour ago

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("Today at 9:15 AM", result)
    }

    @Test
    fun testTodayAtTimePM() {
        val now = createInstant(2025, 1, 15, 22, 30)
        val instant = createInstant(2025, 1, 15, 15, 45) // Same day, afternoon

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("Today at 3:45 PM", result)
    }

    @Test
    fun testTodayAtNoon() {
        val now = createInstant(2025, 1, 15, 18, 0)
        val instant = createInstant(2025, 1, 15, 12, 0) // Noon

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("Today at 12:00 PM", result)
    }

    @Test
    fun testTodayAtMidnight() {
        val now = createInstant(2025, 1, 15, 10, 0)
        val instant = createInstant(2025, 1, 15, 0, 0) // Midnight

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("Today at 12:00 AM", result)
    }

    @Test
    fun testYesterdayAtTime() {
        val now = createInstant(2025, 1, 15, 10, 30)
        val instant = createInstant(2025, 1, 14, 9, 15) // Yesterday

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("Yesterday at 9:15 AM", result)
    }

    @Test
    fun testYesterdayAtTimePM() {
        val now = createInstant(2025, 1, 15, 10, 30)
        val instant = createInstant(2025, 1, 14, 20, 45) // Yesterday evening

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("Yesterday at 8:45 PM", result)
    }

    @Test
    fun testTwoDaysAgo() {
        val now = createInstant(2025, 1, 15, 10, 30)
        val instant = createInstant(2025, 1, 13, 9, 15) // 2 days ago

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("on 01/13", result)
    }

    @Test
    fun testWeekAgo() {
        val now = createInstant(2025, 1, 15, 10, 30)
        val instant = createInstant(2025, 1, 8, 9, 15) // Week ago

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("on 01/08", result)
    }

    @Test
    fun testMonthsAgo() {
        val now = createInstant(2025, 3, 15, 10, 30)
        val instant = createInstant(2025, 1, 10, 9, 15) // Months ago

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("on 01/10", result)
    }

    @Test
    fun testExactlyOneHourShouldShowTodayAt() {
        val now = createInstant(2025, 1, 15, 11, 30)
        val instant = createInstant(2025, 1, 15, 10, 30) // Exactly 1 hour ago

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("Today at 10:30 AM", result)
    }

    @Test
    fun testJustUnderOneHourShouldShowMinutes() {
        val now = createInstant(2025, 1, 15, 11, 29)
        val instant = createInstant(2025, 1, 15, 10, 30) // 59 minutes ago

        val result = getRelativeTimeString(instant, now, timeZone)
        assertEquals("59 minutes ago", result)
    }
}
