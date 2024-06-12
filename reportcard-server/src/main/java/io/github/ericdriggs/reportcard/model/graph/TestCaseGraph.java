package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.xml.testng.suite.Test;
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
) implements TestCaseGraphBuilder.With {

    @JsonIgnore
    public TestCaseModel asTestCaseModel() {
        TestCaseModel testCaseModel = new TestCaseModel();
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
}
