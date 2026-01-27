package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Static utility methods for parsing Karate JSON summary files.
 * Extracts timing data (elapsedTime, resultDate) to calculate start_time and end_time for run records.
 * All methods return null on invalid input (no exceptions propagated).
 */
@Slf4j
public enum KarateConvertersUtil {
    ;//static methods only

    /**
     * Karate date format: "2026-01-20 03:00:56 PM"
     * Uses 12-hour format with AM/PM marker.
     */
    private static final DateTimeFormatter KARATE_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a", Locale.US);

    /**
     * Parses Karate summary JSON content into a KarateSummary object.
     *
     * @param jsonContent the JSON content from karate-summary-json.txt
     * @return populated KarateSummary or null if parsing fails
     */
    public static KarateSummary parseKarateSummary(String jsonContent) {
        if (jsonContent == null || jsonContent.isBlank()) {
            log.warn("Karate summary JSON is null or blank");
            return null;
        }
        return SharedObjectMappers.readValueOrDefault(jsonContent, KarateSummary.class, null);
    }

    /**
     * Parses Karate's resultDate string into a LocalDateTime.
     *
     * @param resultDate the date string in format "yyyy-MM-dd hh:mm:ss a" (e.g., "2026-01-20 03:00:56 PM")
     * @return LocalDateTime or null if parsing fails
     */
    public static LocalDateTime parseResultDate(String resultDate) {
        if (resultDate == null || resultDate.isBlank()) {
            log.warn("Karate resultDate is null or blank");
            return null;
        }
        try {
            return LocalDateTime.parse(resultDate, KARATE_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse Karate resultDate: {}", resultDate, e);
            return null;
        }
    }

    /**
     * Calculates start time by subtracting elapsed time from end time.
     *
     * @param endTime the end time (from resultDate)
     * @param elapsedTimeMillis the elapsed time in milliseconds
     * @return LocalDateTime for start time, or null if inputs are invalid
     */
    public static LocalDateTime calculateStartTime(LocalDateTime endTime, Double elapsedTimeMillis) {
        if (endTime == null) {
            log.warn("Cannot calculate start time: endTime is null");
            return null;
        }
        if (elapsedTimeMillis == null) {
            log.warn("Cannot calculate start time: elapsedTimeMillis is null");
            return null;
        }
        if (elapsedTimeMillis < 0) {
            log.warn("Negative elapsedTimeMillis: {}, treating as null", elapsedTimeMillis);
            return null;
        }

        long elapsedMillisLong = elapsedTimeMillis.longValue();
        return endTime.minusNanos(elapsedMillisLong * 1_000_000);
    }
}
