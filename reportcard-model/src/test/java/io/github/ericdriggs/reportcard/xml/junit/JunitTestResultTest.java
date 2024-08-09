package io.github.ericdriggs.reportcard.xml.junit;

import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.model.converter.junit.JunitConvertersUtil;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class JunitTestResultTest {

    @Test
    void unmarshallXmlTest() {
        TestResultModel testResultModel = fromRelativePath("format-samples/sample-junit.xml");
        assertEquals(66, testResultModel.getTestSuites().size());

        TestSuiteModel testSuite = testResultModel.getTestSuites().get(0);
        assertEquals(3, testSuite.getTests());
        assertEquals(0, testSuite.getError());
        assertEquals(0, testSuite.getFailure());
        assertEquals(0, testSuite.getSkipped());
        //field missing in source xml
        assertNull(testSuite.getHasSkip());
        assertTrue(testSuite.getIsSuccess());
    }

    protected static TestResultModel fromRelativePath(String relativePath) {
        String xmlString = ResourceReader.resourceAsString(relativePath);

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(io.github.ericdriggs.reportcard.xml.junit.Testsuites.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            io.github.ericdriggs.reportcard.xml.junit.Testsuites testsuites = (Testsuites) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            return JunitConvertersUtil.doFromJunitToModelTestResult(testsuites);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }
}
