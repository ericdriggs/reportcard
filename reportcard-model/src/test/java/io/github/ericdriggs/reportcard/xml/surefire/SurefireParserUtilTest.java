package io.github.ericdriggs.reportcard.xml.surefire;

import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SurefireParserUtilTest {

    @Test
    void unmarshallXml() {

        String xmlString = ResourceReader.resourceAsString("format-samples/sample-surefire.xml");
        Testsuite testsuite = SurefireParserUtil.parseTestSuite(xmlString);

        assertEquals(3, testsuite.testcase.size());

    }

}