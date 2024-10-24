package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.soabase.recordbuilder.core.RecordBuilder;

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
