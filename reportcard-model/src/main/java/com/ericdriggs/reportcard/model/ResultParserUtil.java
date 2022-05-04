package com.ericdriggs.reportcard.model;

import com.ericdriggs.reportcard.model.converter.surefire.SurefireConvertersUtil;
import com.ericdriggs.reportcard.xml.surefire.SurefireParserUtil;
import com.ericdriggs.reportcard.xml.surefire.Testsuite;

import java.util.List;

public enum ResultParserUtil {

    ;//static methods only
    private static final String XML_EXTENSION_REGEX = ".*[.]xml";

    public static TestResult fromSurefirePath(String absolutePath) {
        return fromSurefirePathAndRegex(absolutePath, XML_EXTENSION_REGEX);
    }


    public static TestResult fromSurefirePathAndRegex(String absolutePath, String fileNameRegex) {
        List<Testsuite> testsuites = SurefireParserUtil.parseTestSuitesFromPathAndRegex(absolutePath, fileNameRegex);
        return SurefireConvertersUtil.doFromSurefireToModelTestResult(testsuites);
    }
}