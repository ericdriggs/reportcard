package com.ericdriggs.reportcard.xml.testng;

import com.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TestngUnmarshallTest {

    @Autowired
    ResourceReader resourceReader;

    @Test
    void unmarshallXml() {
        String xmlString = resourceReader.resourceAsString("classpath:format-samples/sample-testng.xml");

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(TestngResults.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            TestngResults testngResults = (TestngResults) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            System.out.println(testngResults);
            assertEquals(1, testngResults.suites.size());
            assertEquals(1, testngResults.suites.get(0).listenersOrPackagesOrTest.size());
            com.ericdriggs.reportcard.xml.testng.Test test = (com.ericdriggs.reportcard.xml.testng.Test)  testngResults.suites.get(0).listenersOrPackagesOrTest.get(0);
            assertEquals("Ant test", test.getName() );
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }
}
