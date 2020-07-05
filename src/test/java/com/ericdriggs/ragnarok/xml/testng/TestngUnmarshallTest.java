package com.ericdriggs.ragnarok.xml.testng;

import com.ericdriggs.ragnarok.xml.ResourceReader;
import com.ericdriggs.ragnarok.xml.surefire.Testsuite;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@SpringBootTest
public class TestngUnmarshallTest {

    @Autowired
    ResourceReader resourceReader;

    @Test
    void unmarshallXml() {
        String xmlString = resourceReader.resourceAsString("classpath:format-samples/sample-testng.xml");

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Testsuite.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            TestngResults testngResults = (TestngResults) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            System.out.println(testngResults);
            //TODO: add assertions
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
