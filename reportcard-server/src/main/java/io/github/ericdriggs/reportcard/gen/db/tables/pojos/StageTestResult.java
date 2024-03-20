package io.github.ericdriggs.reportcard.gen.db.tables.pojos;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;

@Value
@Builder
public class StageTestResult implements HasTestResult {

    io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo stage;
    io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo testResult;

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
