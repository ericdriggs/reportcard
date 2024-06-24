package io.github.ericdriggs.reportcard.controller.graph.trend;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.TreeMap;

@Builder
@Jacksonized
@Value
public class TestRunStates {

    TreeMap<Integer, TestCaseRunState> testRunStateMap;
}
