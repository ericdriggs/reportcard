package io.github.ericdriggs.reportcard.controller.graph.trend;

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
    String testPackage;
    String testSuite;
    String testCase;

    FailureMessageIndexMap failureMessageIndexMap;
    Instant failSince;
    BigDecimal avg30;
    BigDecimal avg60;

    TreeSet<TestCaseRunGroupedState> testRunGroupedStates;





}
