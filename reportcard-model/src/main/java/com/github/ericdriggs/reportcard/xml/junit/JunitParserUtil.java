package com.github.ericdriggs.reportcard.xml.junit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class JunitParserUtil {
    private JunitParserUtil() {
        //call statically
    }

    public static Testsuites parseTestSuites(String xmlString) {

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Testsuites.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Testsuites testsuites = (Testsuites) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            return testsuites;

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
