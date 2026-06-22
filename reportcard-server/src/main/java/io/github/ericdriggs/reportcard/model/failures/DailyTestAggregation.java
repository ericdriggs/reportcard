package io.github.ericdriggs.reportcard.model.failures;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Jacksonized
@Value
public class DailyTestAggregation {
    LocalDate date;
    int totalTests;
    int passCount;
    int failCount;
    BigDecimal passPercent;
}
