package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class StageTestResult {

    Stage stage;
    TestResult testResult;

    public boolean isSuccess() {
        if (testResult == null || testResult.getIsSuccess() == null) {
            return false;
        }
        return testResult.getIsSuccess();
    }

    public Duration getDuration() {
        if (testResult == null || testResult.getTime() == null) {
            return null;
        }

        return Duration.ofSeconds(testResult.getTime().longValue());
    }

    public String getDurationString() {
        Duration duration = getDuration();
        if (duration == null) {
            return "";
        }

        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        String hourString = hours > 0 ? hours + "h " : "";
        String minuteString = minutes > 0 || hours > 0 ? minutes + "min " : "";
        String secondString = seconds + "s";
        return hourString + minuteString + secondString;
    }
}
