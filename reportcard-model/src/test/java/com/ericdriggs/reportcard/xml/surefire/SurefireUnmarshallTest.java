package com.ericdriggs.reportcard.xml.surefire;

import com.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SurefireUnmarshallTest {

    @Test
    void unmarshallXml() {
        String xmlString = ResourceReader.resourceAsString("format-samples/sample-surefire.xml");

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Testsuite.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Testsuite testsuite = (Testsuite) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            System.out.println(testsuite);
            assertEquals(3, testsuite.tests);
            assertEquals(3, testsuite.testcase.size());
            assertNotNull(testsuite.testcase.get(0).getSkipped());
            assertNotNull(testsuite.testcase.get(1).getFailure());
            assertNotNull(testsuite.testcase.get(2).getError());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
