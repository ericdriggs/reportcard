package io.github.ericdriggs.reportcard.model.converter.junit;


import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
import io.github.ericdriggs.reportcard.xml.junit.JunitParserUtil;
import io.github.ericdriggs.reportcard.xml.junit.Testsuite;
import io.github.ericdriggs.reportcard.xml.junit.Testsuites;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.xml.junit.Testcase;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class JunitMapperTests {

    protected final static ModelMapper modelMapper = JunitConvertersUtil.modelMapper;
    @Test
    public void testCaseTest() {

        Testcase junitTestCase = JunitFactoryUtil.newTestCase(TestStatus.FAILURE);
        assertNotNull(junitTestCase.getFailure());
        assertNull(junitTestCase.getError());
        assertNull(junitTestCase.getSkipped());

        TestCaseModel modelTestCase = modelMapper.map(junitTestCase, TestCaseModel.class);
        assertEquals(junitTestCase.getClassname(), modelTestCase.getClassName());
        assertEquals(junitTestCase.getName(), modelTestCase.getName());
        assertEquals(junitTestCase.getTime(), modelTestCase.getTime());
        assertEquals(TestStatus.FAILURE, modelTestCase.getTestStatus());
    }

    protected static Testsuite genTestSuite() {
        List<TestStatus> testStatuses = new ArrayList<>();
        testStatuses.add(TestStatus.SUCCESS);
        testStatuses.add(TestStatus.SKIPPED);
        testStatuses.add(TestStatus.FAILURE);
        testStatuses.add(TestStatus.ERROR);

        Testsuite suite = JunitFactoryUtil.newTestSuite(testStatuses);
        return suite;
    }

    @Test
    public void testSuiteTest() {

        Testsuite suite = genTestSuite();
        {
            assertEquals(1, suite.getErrors());
            assertEquals(1, suite.getFailures());
            assertEquals(1, suite.getSkipped());
            assertEquals("com.foo.bar", suite.get_package());
            assertEquals("testSuiteName", suite.getName());

            assertEquals(4, suite.getTests());
            assertEquals(new BigDecimal("4.92"), suite.getTime());
            {
                List<Testcase> testcases = suite.getTestcase();
                //TODO: assert testcases;
            }
        }
//
        TestSuiteModel modelTestSuite = modelMapper.map(suite, TestSuiteModel.class);
        {
            assertEquals(1, modelTestSuite.getError());
            assertEquals(1, modelTestSuite.getFailure());
            assertEquals(1, modelTestSuite.getSkipped());
            assertEquals("com.foo.bar", modelTestSuite.getPackageName());

            assertEquals(4, modelTestSuite.getTests());
            assertEquals(new BigDecimal("4.92"), modelTestSuite.getTime());
            {
                List<TestCaseModel> testcases = modelTestSuite.getTestCases();
                assertEquals(TestStatus.SUCCESS, testcases.get(0).getTestStatus());
                //TODO: assert testcases;
            }
        }

    }

    @Test
    public void testResultTest() {

        Testsuite suite = genTestSuite();

        List<Testsuite> testsuites = new ArrayList<>();
        testsuites.add(suite);
        testsuites.add(suite); //calculations are by value so add same twice is valid
        Testsuites suites = new Testsuites();
        suites.setTestsuite(testsuites);

        TestResultModel modelTestResult = modelMapper.map(suites, TestResultModel.class);

        assertEquals(8, modelTestResult.getTests());
        assertEquals(2, modelTestResult.getError());
        assertEquals(2, modelTestResult.getFailure());
        assertEquals(2, modelTestResult.getSkipped());
    }

    @Test
    void systemOut_systemErr_Assertions_Test() {
        String xmlString = ResourceReader.resourceAsString("format-samples/junit/TEST-io.github.ericdriggs.file.FileUtilsTest.xml");
        Testsuites suites = JunitParserUtil.parseTestSuites(xmlString);


        TestResultModel modelTestResult = modelMapper.map(suites, TestResultModel.class);

        assertEquals(1, modelTestResult.getTestSuites().size());
        assertEquals(2, modelTestResult.getTests());

        TestSuiteModel testSuite = modelTestResult.getTestSuites().get(0);

        assertNotNull(testSuite.getName());
        assertFalse(testSuite.getName().trim().isEmpty());
        assertTrue(testSuite.getSystemOut().contains(testSuite.getName()));
        assertTrue(testSuite.getSystemOut().contains("system-out"));
        assertTrue(testSuite.getSystemErr().contains(testSuite.getName()));
        assertTrue(testSuite.getSystemErr().contains("system-err"));

        for (TestCaseModel testcase : testSuite.getTestCases()) {
            assertNotNull(testcase.getName());
            assertFalse(testcase.getName().trim().isEmpty());
            assertTrue(testcase.getSystemOut().contains(testcase.getName()));
            assertTrue(testcase.getSystemOut().contains("system-out"));
            assertTrue(testcase.getSystemErr().contains(testcase.getName()));
            assertTrue(testcase.getSystemErr().contains("system-err"));
            assertTrue(testcase.getAssertions().contains(testcase.getName()));
            assertTrue(testcase.getAssertions().contains("assertions"));
        }


    }


}
