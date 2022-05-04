package com.github.ericdriggs.reportcard.xml.testng.suite;

import com.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestngUnmarshallTest {

    @Test
    void unmarshallXml() {
        String xmlString = ResourceReader.resourceAsString("format-samples/sample-testng.xml");

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(TestngResults.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            TestngResults testngResults = (TestngResults) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            System.out.println(testngResults);
            assertEquals(1, testngResults.suites.size());
            assertEquals(1, testngResults.suites.get(0).listenersOrPackagesOrTest.size());
            com.github.ericdriggs.reportcard.xml.testng.suite.Test test = (com.github.ericdriggs.reportcard.xml.testng.suite.Test)  testngResults.suites.get(0).listenersOrPackagesOrTest.get(0);
            assertEquals("Ant test", test.getName() );
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }
}
