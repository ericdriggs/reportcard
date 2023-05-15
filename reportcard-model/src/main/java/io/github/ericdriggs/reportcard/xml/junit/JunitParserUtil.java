package io.github.ericdriggs.reportcard.xml.junit;

import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Collections;

public class JunitParserUtil {
    private JunitParserUtil() {
        //call statically
    }

    @SneakyThrows(JAXBException.class)
    public static Testsuites parseTestSuites(String xmlString) {

        if (xmlString.contains("<testsuites")) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Testsuites.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Testsuites testsuites = (Testsuites) unmarshaller.unmarshal(new StringReader(xmlString));
            return testsuites;
        }

        //If only 1 testsuite element, testsuite will be the root element instead of testsuites
        if (xmlString.contains("<testsuite")) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Testsuite.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Testsuite testsuite = (Testsuite) unmarshaller.unmarshal(new StringReader(xmlString));
            Testsuites testsuites = new Testsuites();
            testsuites.setTestsuite(Collections.singletonList(testsuite));
            return testsuites;
        }

        throw new IllegalArgumentException("not a junit xml:\n " + xmlString);
    }
}
