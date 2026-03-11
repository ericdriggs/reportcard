package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder
public record TestCaseGraph(
        Long testCaseId,
        Long testSuiteFk,
        Byte testStatusFk,
        String name,
        String className,
        BigDecimal time,
        String systemOut,
        String systemErr,
        String assertions,
        List<TestCaseFaultGraph> testCaseFaults
) {
    private static final Logger log = LoggerFactory.getLogger(TestCaseGraph.class);

    /**
     * JSON deserializer that derives testStatusFk from testStatus if testStatusFk is null.
     * This handles legacy data where testStatusFk may be null but testStatus string is present.
     */
    @JsonCreator
    public static TestCaseGraph fromJson(
            @JsonProperty("testCaseId") Long testCaseId,
            @JsonProperty("testSuiteFk") Long testSuiteFk,
            @JsonProperty("testStatusFk") Byte testStatusFk,
            @JsonProperty("testStatus") String testStatus,
            @JsonProperty("name") String name,
            @JsonProperty("className") String className,
            @JsonProperty("time") BigDecimal time,
            @JsonProperty("systemOut") String systemOut,
            @JsonProperty("systemErr") String systemErr,
            @JsonProperty("assertions") String assertions,
            @JsonProperty("testCaseFaults") List<TestCaseFaultGraph> testCaseFaults
    ) {
        // Derive testStatusFk from testStatus if not provided
        if (testStatusFk == null && testStatus != null) {
            try {
                testStatusFk = TestStatus.valueOf(testStatus).getStatusId();
            } catch (IllegalArgumentException e) {
                log.warn("Unknown testStatus '{}' for testCase '{}', leaving testStatusFk as null", testStatus, name);
            }
        }
        return new TestCaseGraph(testCaseId, testSuiteFk, testStatusFk, name, className,
                time, systemOut, systemErr, assertions, testCaseFaults);
    }

    @JsonIgnore
    public TestCaseModel asTestCaseModel() {
        TestCaseModel testCaseModel = TestCaseModel.builder().build();
        testCaseModel.setTestCaseId(testCaseId);
        testCaseModel.setTestSuiteFk(testSuiteFk);
        testCaseModel.setTestStatusFk(testStatusFk);
        testCaseModel.setName(name);
        testCaseModel.setClassName(className);
        testCaseModel.setTime(time);
        testCaseModel.setSystemErr(systemErr);
        testCaseModel.setSystemOut(systemOut);
        testCaseModel.setAssertions(assertions);
        testCaseModel.setTestCaseFaults(TestCaseFaultGraph.toTestCaseModels(testCaseFaults));
        return testCaseModel;
    }

    @JsonProperty("testStatus")
    public String getTestStatus() {
        return TestStatus.testStatusNameFromStatusId(testStatusFk);
    }
}
