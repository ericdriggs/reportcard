package io.github.ericdriggs.reportcard.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.model.TestCaseFaultModel;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder

public record TestCaseFaultGraph(
        Long testCaseFaultId,
        Long testCaseFk,
        Byte faultContextFk,
        String type,
        String message,
        String value
) implements TestCaseFaultGraphBuilder.With {

    @JsonIgnore
    public static List<TestCaseFaultModel> toTestCaseModels(List<TestCaseFaultGraph> testCaseFaultGraphs) {
        List<TestCaseFaultModel> ts = new ArrayList<>();
        if (!CollectionUtils.isEmpty(testCaseFaultGraphs)) {
            for (TestCaseFaultGraph t : testCaseFaultGraphs) {
                ts.add(t.asTestCaseFaultModel());
            }
        }
        return ts;
    }

    @JsonIgnore
    public TestCaseFaultModel asTestCaseFaultModel() {
        TestCaseFaultModel t = new TestCaseFaultModel();
        t.setTestCaseFaultId(testCaseFaultId);
        t.setTestCaseFk(testCaseFk);
        t.setFaultContextFk(faultContextFk);
        t.setType(type);
        t.setMessage(message);
        t.setValue(value);
        return t;
    }
}
