package io.github.ericdriggs.reportcard.xml.junit;

import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void systemOut_systemErr_assertions_Test() {

        String xmlString = ResourceReader.resourceAsString("format-samples/junit/TEST-io.github.ericdriggs.file.FileUtilsTest.xml");
        Testsuites testsuites = JunitParserUtil.parseTestSuites(xmlString);

        assertEquals(1, testsuites.testsuite.size());
        int testCount = 0;
        for (Testsuite t : testsuites.testsuite) {
            testCount += t.tests;
        }
        assertEquals(2, testCount);

        Testsuite testsuite = testsuites.getTestsuite().get(0);
        for (Testcase testcase : testsuite.testcase) {

            assertNotNull(testcase.getName());
            assertFalse(testcase.getName().trim().isEmpty());
            assertTrue(testcase.systemOut.contains(testcase.name));
            assertTrue(testcase.systemOut.contains("system-out"));
            assertTrue(testcase.systemErr.contains(testcase.name));
            assertTrue(testcase.systemErr.contains("system-err"));
            assertTrue(testcase.assertions.contains(testcase.name));
            assertTrue(testcase.assertions.contains("assertions"));
        }

    }

}