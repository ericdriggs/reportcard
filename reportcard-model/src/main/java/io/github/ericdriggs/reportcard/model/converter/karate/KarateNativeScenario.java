package io.github.ericdriggs.reportcard.model.converter.karate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(access = AccessLevel.PACKAGE)
public class KarateNativeScenario {
    String name;
    Integer line;
    Long startTime;
    Long endTime;
}
