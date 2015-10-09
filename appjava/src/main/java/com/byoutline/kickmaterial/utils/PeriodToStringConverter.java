package com.byoutline.kickmaterial.utils;

import com.byoutline.kickmaterial.model.ProjectTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import javax.annotation.Nonnull;

/**
 * Converts {@link org.joda.time.Period} to String that shows non zero natural number in greatest of unit from
 * fallowing set:<br />
 * day, hour, minute, second.
 */
public final class PeriodToStringConverter {
    private PeriodToStringConverter() {
        // static methods only
    }

    private static final PeriodFormatter DAY_FORMATTER = new PeriodFormatterBuilder().appendDays().appendSuffix("day", " days").toFormatter();
    private static final PeriodFormatter HOUR_FORMATTER = new PeriodFormatterBuilder().appendHours().appendSuffix("hour", " hours").toFormatter();
    private static final PeriodFormatter MINUTE_FORMATTER = new PeriodFormatterBuilder().appendMinutes().appendSuffix("minute", " minutes").toFormatter();
    private static final PeriodFormatter SECOND_FORMATTER = new PeriodFormatterBuilder().appendSeconds().appendSuffix(" second", " seconds").toFormatter();
    private static final String AUCTION_ENDED = "ENDED";

    /**
     * @param daysPeriod {@link org.joda.time.Period} of type {@link org.joda.time.PeriodType#dayTime()}
     * @return Formatted time remaining, or information that auction ended.
     */
    public static String periodToString(Period daysPeriod) {
        PeriodFormatterAndValue formatterToUse = selectFormatter(daysPeriod);
        if (formatterToUse == null) {
            return AUCTION_ENDED;
        }
        return formatterToUse.formatter.print(daysPeriod);
    }

    public static ProjectTime periodToProjectTime(Period daysPeriod) {
        PeriodFormatterAndValue formatterToUse = selectFormatter(daysPeriod);
        if (formatterToUse == null) {
            return null;
        }
        String valueString = Integer.toString(formatterToUse.value);
        String formattedValue = formatterToUse.formatter.print(daysPeriod);
        String formattedStringWithoutValue = formattedValue.replaceFirst(valueString, "").trim();
        return new ProjectTime(valueString, formattedStringWithoutValue + " left");
    }

    /**
     * Best matching formatter of null if auction ended.
     *
     * @param daysPeriod
     * @return
     */
    private static PeriodFormatterAndValue selectFormatter(Period daysPeriod) {
        int days = daysPeriod.getDays();
        if (days > 0) {
            return pav(DAY_FORMATTER, days);
        }
        int hours = daysPeriod.getHours();
        if (hours > 0) {
            return pav(HOUR_FORMATTER, hours);
        }
        int minutes = daysPeriod.getMinutes();
        if (minutes > 0) {
            return pav(MINUTE_FORMATTER, minutes);
        }
        int seconds = daysPeriod.getSeconds();
        if (seconds > 0) {
            return pav(SECOND_FORMATTER, seconds);
        }
        return null;
    }

    /**
     * Shorter syntax for creating {@link PeriodFormatterAndValue}
     *
     * @param formatter
     * @param value
     * @return
     */
    private static PeriodFormatterAndValue pav(@Nonnull PeriodFormatter formatter, int value) {
        return new PeriodFormatterAndValue(formatter, value);
    }
}

class PeriodFormatterAndValue {
    @Nonnull
    public final PeriodFormatter formatter;
    public final int value;

    public PeriodFormatterAndValue(@Nonnull PeriodFormatter formatter, int value) {
        this.formatter = formatter;
        this.value = value;
    }
}
