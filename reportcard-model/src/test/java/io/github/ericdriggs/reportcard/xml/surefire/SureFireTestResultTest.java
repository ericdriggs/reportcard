package io.github.ericdriggs.reportcard.xml.surefire;

import io.github.ericdriggs.reportcard.dto.TestCase;
import io.github.ericdriggs.reportcard.dto.TestResult;
import io.github.ericdriggs.reportcard.dto.TestSuite;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.model.converter.surefire.SurefireConvertersUtil;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SureFireTestResultTest {

    @Test
    void unmarshallXmlTest() {
        TestResultModel testResultModel = fromRelativePath("format-samples/sample-surefire.xml");
        assertEquals(1, testResultModel.getTestSuites().size());

        TestSuiteModel testSuite = testResultModel.getTestSuites().get(0);
        assertEquals(3, testSuite.getTests());
        assertEquals(1, testSuite.getError());
        assertEquals(1, testSuite.getFailure());
        assertEquals(1, testSuite.getSkipped());
        assertTrue(testSuite.getHasSkip());
        assertFalse(testSuite.getIsSuccess());


        {
            TestCaseModel testCaseModel = testSuite.getTestCases().get(0);
            assertEquals(TestStatus.SKIPPED, testCaseModel.getTestStatus());
        }
        {
            TestCaseModel testCaseModel = testSuite.getTestCases().get(1);
            assertEquals(TestStatus.FAILURE, testCaseModel.getTestStatus());
        }
        {
            TestCaseModel testCaseModel = testSuite.getTestCases().get(2);
            assertEquals(TestStatus.ERROR, testCaseModel.getTestStatus());
        }

    }

    protected static TestResultModel fromRelativePath(String relativePath) {
        String xmlString = ResourceReader.resourceAsString(relativePath);

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Testsuite.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Testsuite testsuite = (Testsuite) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));

            return SurefireConvertersUtil.doFromSurefireToModelTestResult(Collections.singleton(testsuite));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }
}
