package com.ericdriggs.reportcard.model;

import java.util.HashMap;
import java.util.Map;

public enum TestStatus {
    SUCCESS(0),
    SKIPPED(1),
    FAILURE(2),
    ERROR(3);

    TestStatus(int statusId) {
        this.statusId = statusId;

    }

    private final int statusId;

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
