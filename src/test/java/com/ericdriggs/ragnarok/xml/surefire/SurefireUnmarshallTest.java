package com.ericdriggs.ragnarok.xml.surefire;

import com.ericdriggs.ragnarok.xml.ResourceReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@SpringBootTest
public class SurefireUnmarshallTest {

    @Autowired
    ResourceReader resourceReader;

    @Test
    void unmarshallXml() {
        String xmlString = resourceReader.resourceAsString("classpath:format-samples/sample-surefire.xml");

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Testsuite.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Testsuite testsuite = (Testsuite) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            System.out.println(testsuite);
            //TODO: add assertions
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
