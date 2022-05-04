package com.github.ericdriggs.reportcard.model.converter.junit;


import com.github.ericdriggs.reportcard.model.TestCase;
import com.github.ericdriggs.reportcard.model.TestStatus;
import com.github.ericdriggs.reportcard.xml.junit.Testsuite;
import com.github.ericdriggs.reportcard.xml.junit.Testsuites;
import com.github.ericdriggs.reportcard.model.TestResult;
import com.github.ericdriggs.reportcard.model.TestSuite;
import com.github.ericdriggs.reportcard.xml.junit.Testcase;
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

        TestCase modelTestCase = modelMapper.map(junitTestCase, TestCase.class);
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
        TestSuite modelTestSuite = modelMapper.map(suite, TestSuite.class);
        {
            assertEquals(1, modelTestSuite.getError());
            assertEquals(1, modelTestSuite.getFailure());
            assertEquals(1, modelTestSuite.getSkipped());
            assertEquals("com.foo.bar", modelTestSuite.getPackage());

            assertEquals(4, modelTestSuite.getTests());
            assertEquals(new BigDecimal("4.92"), modelTestSuite.getTime());
            {
                List<TestCase> testcases = modelTestSuite.getTestCases();
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

        TestResult modelTestResult = modelMapper.map(suites, TestResult.class);

        assertEquals(8, modelTestResult.getTests());
        assertEquals(2, modelTestResult.getError());
        assertEquals(2, modelTestResult.getFailure());
        assertEquals(2, modelTestResult.getSkipped());
    }


}
