package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.xml.ResultCount;

import java.util.HashMap;
import java.util.Map;

public enum TestStatus {
    SUCCESS(1, TestStatusType.SUCCESS),
    SKIPPED(2, TestStatusType.SKIPPED),
    FAILURE(3, TestStatusType.FAILURE),
    ERROR(4, TestStatusType.ERROR),
    FLAKY_FAILURE(5, TestStatusType.FAILURE),
    RERUN_FAILURE(6, TestStatusType.FAILURE),
    FLAKY_ERROR(7, TestStatusType.ERROR),
    RERUN_ERROR(8, TestStatusType.ERROR);

    TestStatus(int statusId, TestStatusType testStatusType) {
        this.statusId = statusId;
        this.testStatusType = testStatusType;

    }

    private final int statusId;
    private final TestStatusType testStatusType;

    public Integer getStatusId() {
        return statusId;
    }

    private static final Map<Integer, TestStatus> testStatusMap = new HashMap<>();

    private static void initMap() {
        if (testStatusMap.isEmpty()) {
            synchronized ("TestStatus.initMap") {
                for (TestStatus testStatus : TestStatus.values()) {
                    testStatusMap.put(testStatus.getStatusId(), testStatus);
                }
            }
        }
    }

    public TestStatusType getTestStatusType() {
        return testStatusType;
    }

    public static TestStatus fromStatusId(int statusId) {
        initMap();
        TestStatus testStatus = testStatusMap.get(statusId);
        if (testStatus == null) {
            throw new IllegalArgumentException("statusId not found: " + statusId);
        }
        return testStatus;
    }

    public ResultCount getResultCount() {
        return this.getTestStatusType().getResultCount();
    }
}
