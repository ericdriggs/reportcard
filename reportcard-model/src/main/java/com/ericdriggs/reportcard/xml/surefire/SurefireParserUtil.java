package com.ericdriggs.reportcard.xml.surefire;


import com.ericdriggs.file.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SurefireParserUtil {
    private SurefireParserUtil() {
        //call statically
    }


    public static List<Testsuite> parseTestSuites(String relativePath, String fileNameRegex) {
        List<String> xmlStringList = FileUtils.fileContentsFromRelativeDir(relativePath, fileNameRegex);
        return parseTestSuites(xmlStringList);
    }

    public static List<Testsuite> parseTestSuites(List<String> xmlStringList) {
        List<Testsuite> testsuites = new ArrayList<>();
        for ( String xmlString : xmlStringList) {
            testsuites.add(parseTestSuite(xmlString));
        }
        return testsuites;
    }

    public static Testsuite parseTestSuite(String xmlString) {

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Testsuite.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Testsuite testsuite = (Testsuite) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            return testsuite;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
