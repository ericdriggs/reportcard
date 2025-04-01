package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.PojoComparators;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo;
import lombok.Builder;
import lombok.Value;

import java.time.Duration;

@Value
@Builder
public class StageTestResultPojo implements Comparable<StageTestResultPojo> {
    StagePojo stage;
    TestResultPojo testResultPojo;

    public boolean isSuccess() {
        if (testResultPojo == null || testResultPojo.getIsSuccess() == null) {
            return false;
        }
        return testResultPojo.getIsSuccess();
    }

    public Duration getDuration() {
        if (testResultPojo == null || testResultPojo.getTime() == null) {
            return null;
        }

        return Duration.ofSeconds(testResultPojo.getTime().longValue());
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

    @Override
    public int compareTo(StageTestResultPojo that) {
        return PojoComparators.compareStageTestResultPojoDateDescending(this, that);
    }

    public int getTestCount() {
        if (testResultPojo == null || testResultPojo.getTests() == null ) {
            return 0;
        }
        return testResultPojo.getTests();
    }
}
