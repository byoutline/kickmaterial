package com.byoutline.kickmaterial.model.utils

import com.byoutline.kickmaterial.model.ProjectTime
import org.joda.time.Period
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder

/**
 * Converts [org.joda.time.Period] to String that shows non zero natural number in greatest of unit from
 * fallowing set:<br></br>
 * day, hour, minute, second.
 */
object PeriodToStringConverter {

    private val DAY_FORMATTER = PeriodFormatterBuilder().appendDays().appendSuffix("day", " days").toFormatter()
    private val HOUR_FORMATTER = PeriodFormatterBuilder().appendHours().appendSuffix("hour", " hours").toFormatter()
    private val MINUTE_FORMATTER = PeriodFormatterBuilder().appendMinutes().appendSuffix("minute", " minutes").toFormatter()
    private val SECOND_FORMATTER = PeriodFormatterBuilder().appendSeconds().appendSuffix(" second", " seconds").toFormatter()
    private val AUCTION_ENDED = "ENDED"

    /**
     * @param daysPeriod [org.joda.time.Period] of type [org.joda.time.PeriodType.dayTime]
     * *
     * @return Formatted time remaining, or information that auction ended.
     */
    fun periodToString(daysPeriod: Period): String {
        val formatterToUse = selectFormatter(daysPeriod) ?: return AUCTION_ENDED
        return formatterToUse.formatter.print(daysPeriod)
    }

    fun periodToProjectTime(daysPeriod: Period): ProjectTime? {
        val formatterToUse = selectFormatter(daysPeriod) ?: return null
        val valueString = Integer.toString(formatterToUse.value)
        val formattedValue = formatterToUse.formatter.print(daysPeriod)
        val formattedStringWithoutValue = formattedValue.replaceFirst(valueString.toRegex(), "").trim { it <= ' ' }
        return ProjectTime(valueString, formattedStringWithoutValue + " left")
    }

    /**
     * Best matching formatter of null if auction ended.

     * @param daysPeriod
     * *
     * @return
     */
    private fun selectFormatter(daysPeriod: Period): PeriodFormatterAndValue? {
        val days = daysPeriod.days
        if (days > 0) {
            return pav(DAY_FORMATTER, days)
        }
        val hours = daysPeriod.hours
        if (hours > 0) {
            return pav(HOUR_FORMATTER, hours)
        }
        val minutes = daysPeriod.minutes
        if (minutes > 0) {
            return pav(MINUTE_FORMATTER, minutes)
        }
        val seconds = daysPeriod.seconds
        if (seconds > 0) {
            return pav(SECOND_FORMATTER, seconds)
        }
        return null
    }

    /**
     * Shorter syntax for creating [PeriodFormatterAndValue]

     * @param formatter
     * *
     * @param value
     * *
     * @return
     */
    private fun pav(formatter: PeriodFormatter, value: Int): PeriodFormatterAndValue {
        return PeriodFormatterAndValue(formatter, value)
    }
}// static methods only

internal class PeriodFormatterAndValue(val formatter: PeriodFormatter, val value: Int)
