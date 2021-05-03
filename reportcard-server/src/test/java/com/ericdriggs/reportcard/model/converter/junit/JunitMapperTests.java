package com.ericdriggs.reportcard.model.converter.junit;


import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.xml.junit.Testsuite;
import com.ericdriggs.reportcard.xml.junit.Testsuites;
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

        com.ericdriggs.reportcard.xml.junit.Testcase junitTestCase = JunitFactoryUtil.newTestCase(TestStatus.FAILURE);
        assertNotNull(junitTestCase.getFailure());
        assertNull(junitTestCase.getError());
        assertNull(junitTestCase.getSkipped());

        com.ericdriggs.reportcard.model.TestCase modelTestCase = modelMapper.map(junitTestCase, com.ericdriggs.reportcard.model.TestCase.class);
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

        com.ericdriggs.reportcard.xml.junit.Testsuite suite = JunitFactoryUtil.newTestSuite(testStatuses);
        return suite;
    }

    @Test
    public void testSuiteTest() {

        com.ericdriggs.reportcard.xml.junit.Testsuite suite = genTestSuite();
        {
            assertEquals(1, suite.getErrors());
            assertEquals(1, suite.getFailures());
            assertEquals(1, suite.getSkipped());
            assertEquals("com.foo.bar", suite.get_package());
            assertEquals("testSuiteName", suite.getName());

            assertEquals(4, suite.getTests());
            assertEquals(new BigDecimal("4.92"), suite.getTime());
            {
                List<com.ericdriggs.reportcard.xml.junit.Testcase> testcases = suite.getTestcase();
                //TODO: assert testcases;
            }
        }
//
        com.ericdriggs.reportcard.model.TestSuite modelTestSuite = modelMapper.map(suite, com.ericdriggs.reportcard.model.TestSuite.class);
        {
            assertEquals(1, modelTestSuite.getError());
            assertEquals(1, modelTestSuite.getFailure());
            assertEquals(1, modelTestSuite.getSkipped());
            assertEquals("com.foo.bar", modelTestSuite.getPackage());

            assertEquals(4, modelTestSuite.getTests());
            assertEquals(new BigDecimal("4.92"), modelTestSuite.getTime());
            {
                List<com.ericdriggs.reportcard.model.TestCase> testcases = modelTestSuite.getTestCases();
                assertEquals(TestStatus.SUCCESS, testcases.get(0).getTestStatus());
                //TODO: assert testcases;
            }
        }

    }

    @Test
    public void testResultTest() {

        com.ericdriggs.reportcard.xml.junit.Testsuite suite = genTestSuite();

        List<Testsuite> testsuites = new ArrayList<>();
        testsuites.add(suite);
        testsuites.add(suite); //calculations are by value so add same twice is valid
        com.ericdriggs.reportcard.xml.junit.Testsuites suites = new Testsuites();
        suites.setTestsuite(testsuites);

        com.ericdriggs.reportcard.model.TestResult modelTestResult = modelMapper.map(suites, com.ericdriggs.reportcard.model.TestResult.class);

        assertEquals(8, modelTestResult.getTests());
        assertEquals(2, modelTestResult.getError());
        assertEquals(2, modelTestResult.getFailure());
        assertEquals(2, modelTestResult.getSkipped());
    }


}
