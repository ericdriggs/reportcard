package io.github.ericdriggs.reportcard.model.converter.surefire;

import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.xml.surefire.SurefireParserUtil;
import io.github.ericdriggs.reportcard.xml.surefire.Testsuite;

import java.util.List;

public enum SureFireResultParserUtil {

    ;//static methods only
    private static final String XML_EXTENSION_REGEX = ".*[.]xml";

    public static TestResultModel fromSurefirePath(String absolutePath) {
        return fromSurefirePathAndRegex(absolutePath, XML_EXTENSION_REGEX);
    }


    public static TestResultModel fromSurefirePathAndRegex(String absolutePath, String fileNameRegex) {
        List<Testsuite> testsuites = SurefireParserUtil.parseTestSuitesFromPathAndRegex(absolutePath, fileNameRegex);
        return SurefireConvertersUtil.doFromSurefireToModelTestResult(testsuites);
    }
}