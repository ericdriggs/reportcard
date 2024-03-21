package io.github.ericdriggs.reportcard.xml;

import io.github.ericdriggs.reportcard.model.TestResultModel;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JunitSurefireXmlParseUtilTest {

    @Test
    void mixedJunitSurefireTest() {
        String sureFireTestSuiteXml = ResourceReader.resourceAsString("format-samples/sample-surefire.xml");
        String junitTestSuiteXml = ResourceReader.resourceAsString("format-samples/sample-junit-small.xml");
        String junitTestSuitesXml = ResourceReader.resourceAsString("format-samples/sample-junit-testsuites-small.xml");

        List<String> testXmlContents = List.of(junitTestSuiteXml, junitTestSuitesXml, sureFireTestSuiteXml);
        TestResultModel testResultModel = JunitSurefireXmlParseUtil.parseTestXml(testXmlContents);
        assertEquals(4, testResultModel.getTestSuites().size());
        assertEquals(4, testResultModel.getTestSuites().size());
    }
}
