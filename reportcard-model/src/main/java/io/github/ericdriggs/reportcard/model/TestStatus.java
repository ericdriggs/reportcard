package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.xml.ResultCount;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public enum TestStatus {
    SUCCESS((byte)1, TestStatusType.SUCCESS),
    SKIPPED((byte)2, TestStatusType.SKIPPED),
    FAILURE((byte)3, TestStatusType.FAILURE),
    ERROR((byte)4, TestStatusType.ERROR),
    FLAKY_FAILURE((byte)5, TestStatusType.FAILURE),
    RERUN_FAILURE((byte)6, TestStatusType.FAILURE),
    FLAKY_ERROR((byte)7, TestStatusType.ERROR),
    RERUN_ERROR((byte)8, TestStatusType.ERROR);

    TestStatus(byte statusId, TestStatusType testStatusType) {
        this.statusId = statusId;
        this.testStatusType = testStatusType;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isErrorOrFailure() {
        return testStatusType == TestStatusType.ERROR || testStatusType == TestStatusType.FAILURE;
    }

    public boolean isSkipped() {
        return testStatusType == TestStatusType.SKIPPED;
    }
    private final byte statusId;
    private final TestStatusType testStatusType;

    public byte getStatusId() {
        return statusId;
    }

    private static final Map<Byte, TestStatus> testStatusMap = new HashMap<>();

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

    public static TestStatus fromStatusId(byte statusId) {
        initMap();
        TestStatus testStatus = testStatusMap.get(statusId);
        if (testStatus == null) {
            throw new IllegalArgumentException("statusId not found: " + statusId);
        }
        return testStatus;
    }

    public static String testStatusNameFromStatusId(byte statusId) {
        try {
            return TestStatus.fromStatusId(statusId).name();
        } catch (Exception ex) {
            return "" + statusId;
        }
    }

    public ResultCount getResultCount(BigDecimal time) {
        return this.getTestStatusType().getResultCount(time);
    }
}
