package io.github.ericdriggs.reportcard.xml.junit;

import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JunitParserUtilTest {

    @Test
    void unmarshallXml() {

        String xmlString = ResourceReader.resourceAsString("format-samples/sample-junit.xml");
        Testsuites testsuites = JunitParserUtil.parseTestSuites(xmlString);

        assertEquals(66, testsuites.testsuite.size());
        int testCount = 0;
        for (Testsuite t : testsuites.testsuite) {
            testCount += t.tests;
        }
        assertEquals(685, testCount);
    }
}
