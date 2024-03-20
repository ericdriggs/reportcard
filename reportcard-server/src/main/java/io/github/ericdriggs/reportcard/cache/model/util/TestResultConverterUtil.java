package io.github.ericdriggs.reportcard.cache.model.util;

import io.github.ericdriggs.reportcard.model.TestResult;

import java.util.List;
import java.util.Map;

public enum TestResultConverterUtil {
    ;//static methods only

    public static TestResult fromHasTestResultMap(
            Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.HasTestResult,
                    Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuitePojo,
                            Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCasePojo,
                                    List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCaseFaultPojo>>>> hasTestResultMapMap) {
        throw new UnsupportedOperationException("not yet implemented -- blocked on https://github.com/ericdriggs/reportcard/issues/86");
    }

    public static TestResult fromStageTestResultMap(
            Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.StageTestResult,
                    Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuitePojo,
                            Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCasePojo,
                                    List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCaseFaultPojo>>>> testResultMap) {

        throw new UnsupportedOperationException("not yet implemented -- blocked on https://github.com/ericdriggs/reportcard/issues/86");
//        if (testResultMap == null || testResultMap.isEmpty()) {
//            throw new IllegalArgumentException("missing testResultMap");
//        }
//
//        if (testResultMap.size() > 1) {
//            throw new IllegalArgumentException("only singe testResult allowed in map");
//        }
//
//        for (Map.Entry<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult, Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite, Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCase, List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCaseFault>>>> s  : testResultMap.entrySet()) {
//            test
//        }
//        TestResult testResult = new TestResult()
    }

}
