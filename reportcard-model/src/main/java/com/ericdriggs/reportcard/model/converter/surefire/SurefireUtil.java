package com.ericdriggs.reportcard.model.converter.surefire;

import com.ericdriggs.reportcard.model.TestResult;
import com.ericdriggs.reportcard.xml.surefire.SurefireParserUtil;
import com.ericdriggs.reportcard.xml.surefire.Testsuite;

import java.util.List;

public enum SurefireUtil {

    ;//static methods only
    private static final String XML_EXTENSION_REGEX = ".*[.]xml";

    public static TestResult fromPath(String absolutePath) {
        return fromAbsolutePath(absolutePath, XML_EXTENSION_REGEX);
    }


    public static TestResult fromAbsolutePath(String absolutePath, String fileNameRegex) {
        List<Testsuite> testsuites = SurefireParserUtil.parseTestSuitesFromPathAndRegex(absolutePath, fileNameRegex);
        return SurefireConvertersUtil.doFromSurefireToModelTestResult(testsuites);
    }
}