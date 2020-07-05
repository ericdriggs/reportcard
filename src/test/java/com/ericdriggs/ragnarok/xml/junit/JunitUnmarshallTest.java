package com.ericdriggs.ragnarok.xml.junit;

import com.ericdriggs.ragnarok.xml.ResourceReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@SpringBootTest
public class JunitUnmarshallTest {

    @Autowired
    ResourceReader resourceReader;

    @Test
    void unmarshallXml() {

        String xmlString = resourceReader.resourceAsString("classpath:format-samples/sample-junit.xml");

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Testsuites.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Testsuites testsuites = (Testsuites) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            System.out.println(testsuites);
            //TODO: add assertions
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
