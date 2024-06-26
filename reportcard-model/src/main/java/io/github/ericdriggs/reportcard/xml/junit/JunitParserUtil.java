package io.github.ericdriggs.reportcard.xml.junit;

import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JunitParserUtil {
    private JunitParserUtil() {
        //call statically
    }

    public static Testsuites parseTestSuites(String xmlTestSuites) {

        if (xmlTestSuites.contains("<testsuites")) {
            return xmlToTestSuites(xmlTestSuites);
        }

        //If only 1 testsuite element, testsuite will be the root element instead of testsuites
        if (xmlTestSuites.contains("<testsuite")) {
            Testsuites testsuites = new Testsuites();
            testsuites.setTestsuite(Collections.singletonList(xmlToTestSuite(xmlTestSuites)));
            return testsuites;
        }

        throw new IllegalArgumentException("not a junit xml:\n " + xmlTestSuites);
    }

//    public static Testsuites parseTestSuiteAsSuites(String xmlTestSuite) {
//        List<Testsuite> testsuiteList = new ArrayList<>();
//        testsuiteList.add(xmlToTestSuite(xmlTestSuite));
//        Testsuites testsuites = new Testsuites();
//        testsuites.setTestsuite(testsuiteList);
//        return testsuites;
//    }


    @SneakyThrows(JAXBException.class)
    protected static Testsuite xmlToTestSuite(String xmlString) {
        if (xmlString.contains("<testsuite")) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Testsuite.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (Testsuite) unmarshaller.unmarshal(new StringReader(xmlString));
        }
        throw new IllegalArgumentException("not a junit testsuite xml:\n " + xmlString);
    }

    @SneakyThrows(JAXBException.class)
    protected static Testsuites xmlToTestSuites(String xmlString) {

        if (xmlString.contains("<testsuites")) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Testsuites.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Testsuites testsuites = (Testsuites) unmarshaller.unmarshal(new StringReader(xmlString));
            return testsuites;
        }

        throw new IllegalArgumentException("not a junit testsuites xml:\n " + xmlString);
    }
}
