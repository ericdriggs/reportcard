package com.ericdriggs.reportcard.model;

import java.util.HashMap;
import java.util.Map;

public enum TestStatus {
    SUCCESS(0, TestStatusType.SUCCESS),
    SKIPPED(1, TestStatusType.SKIPPED),
    FAILURE(2, TestStatusType.FAILURE),
    ERROR(3, TestStatusType.ERROR),
    FLAKY_FAILURE(4, TestStatusType.FAILURE),
    RERUN_FAILURE(5, TestStatusType.FAILURE),
    FLAKY_ERROR(6, TestStatusType.ERROR),
    RERUN_ERROR(7, TestStatusType.ERROR);

    TestStatus(int statusId, TestStatusType testStatusType) {
        this.statusId = statusId;
        this.testStatusType = testStatusType;

    }

    private final int statusId;
    private TestStatusType testStatusType;

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

    public static TestStatus fromStatusId(int statusId) {
        initMap();
        TestStatus testStatus = testStatusMap.get(statusId);
        if (testStatus == null) {
            throw new IllegalArgumentException("statusId not found: " + statusId);
        }
        return testStatus;
    }

}
