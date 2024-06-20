package io.github.ericdriggs.reportcard.controller.graph.trend;

import io.github.ericdriggs.reportcard.model.trend.TestPackageSuiteCase;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Builder
@Jacksonized
@Value
public class TestCaseTrendRow {
    TestPackageSuiteCase testPackageSuiteCase;
    FailureMessageIndexMap failureMessageIndexMap;
    Instant failSince;
    BigDecimal avg30;
    BigDecimal avgTotal;
    boolean hasSkip;
    TreeSet<TestCaseRunGroupedState> testRunGroupedStates;
}
