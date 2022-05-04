package com.ericdriggs.reportcard.model.converter.surefire;

import com.ericdriggs.reportcard.model.TestResult;
import com.ericdriggs.reportcard.xml.surefire.SurefireParserUtil;
import com.ericdriggs.reportcard.xml.surefire.Testsuite;

import java.util.List;

public enum SurefireUtil {

    ;//static methods only
    private static final String XML_EXTENSION_REGEX = ".*[.]xml";

    public static TestResult fromRelativePath(String relativePath) {
        return fromRelativePath(relativePath, XML_EXTENSION_REGEX);
    }

    public static TestResult fromRelativePath(String relativePath, String fileNameRegex) {
        List<Testsuite> testsuites = SurefireParserUtil.parseTestSuites(relativePath, fileNameRegex);
        return SurefireConvertersUtil.doFromSurefireToModelTestResult(testsuites);

    }
}