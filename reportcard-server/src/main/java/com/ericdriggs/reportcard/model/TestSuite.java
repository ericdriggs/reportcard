package com.ericdriggs.reportcard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestSuite extends com.ericdriggs.reportcard.db.tables.pojos.TestSuite {
    private List<TestCase> testCases = new ArrayList<>();

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public TestSuite setTestCases( List<TestCase> testCases) {
        this.testCases = testCases;
        return this;
    }

//    /**
//     * Gets value from super or calculates it (super may be <code>null</code>> if row just inserted)
//     * @return whether was successful
//     */
//    @Override
//    public Boolean getIsSuccess() {
//        if (super.getIsSuccess() != null) {
//            return super.getIsSuccess();
//        } else {
//            int failure = Objects.requireNonNullElse(super.getFailure(), 0);
//            int error = Objects.requireNonNullElse(super.getError(), 0);
//            int skipped = Objects.requireNonNullElse(super.getSkipped(), 0);
//
//            return failure + error + skipped == 0;
//        }
//    }
//
//    /**
//     * Gets value from super or calculates it (super may be <code>null</code>> if row just inserted)
//     * @return whether has skip
//     */
//    @Override
//    public Boolean getHasSkip() {
//        if (super.getHasSkip() != null) {
//            return super.getHasSkip();
//        } else {
//            int skipped = Objects.requireNonNullElse(super.getSkipped(), 0);
//            return skipped > 0;
//        }
//    }
    //TODO: serialize and desrialize properties from json to Map<String,String>
}
