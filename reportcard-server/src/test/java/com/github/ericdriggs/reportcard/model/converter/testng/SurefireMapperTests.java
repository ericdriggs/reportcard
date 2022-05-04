//package com.github.ericdriggs.reportcard.model.converter.testng;
//
//import com.github.ericdriggs.reportcard.model.TestStatus;
//import com.github.ericdriggs.reportcard.model.converter.surefire.SurefireConvertersUtil;
//import com.github.ericdriggs.reportcard.xml.surefire.Testsuite;
//import org.junit.jupiter.api.Test;
//import org.modelmapper.ModelMapper;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.github.ericdriggs.reportcard.model.converter.surefire.SurefireConvertersUtil.doFromSurefireToModelTestResult;
//import static org.junit.jupiter.api.Assertions.*;
//
//
//public class SurefireMapperTests {
//
//    private static ModelMapper modelMapper = SurefireConvertersUtil.modelMapper;
//
//    @Test
//    public void testCaseTest() {
//
//        com.github.ericdriggs.reportcard.xml.surefire.Testcase surefireTestCase = TestngFactoryUtil.newTestCase(TestStatus.FAILURE);
//        assertNotNull(surefireTestCase.getFailure());
//        assertNull(surefireTestCase.getError());
//        assertNull(surefireTestCase.getSkipped());
//
//        com.github.ericdriggs.reportcard.model.TestCase modelTestCase = modelMapper.map(surefireTestCase, com.github.ericdriggs.reportcard.model.TestCase.class);
//        assertEquals(surefireTestCase.getClassname(), modelTestCase.getClassName());
//        assertEquals(surefireTestCase.getName(), modelTestCase.getName());
//        assertEquals(surefireTestCase.getTime(), modelTestCase.getTime().toPlainString());
//        assertEquals(TestStatus.FAILURE, modelTestCase.getTestStatus());
//    }
//
//    protected static Testsuite genTestSuite() {
//        List<TestStatus> testStatuses = new ArrayList<>();
//        testStatuses.add(TestStatus.SUCCESS);
//        testStatuses.add(TestStatus.SKIPPED);
//        testStatuses.add(TestStatus.FAILURE);
//        testStatuses.add(TestStatus.ERROR);
//        testStatuses.add(TestStatus.FLAKY_FAILURE);
//        testStatuses.add(TestStatus.RERUN_FAILURE);
//        testStatuses.add(TestStatus.FLAKY_ERROR);
//        testStatuses.add(TestStatus.RERUN_ERROR);
//
//        Testsuite suite = TestngFactoryUtil.newTestSuite(testStatuses);
//        return suite;
//    }
//
//    @Test
//    public void testSuiteTest() {
//
//        Testsuite suite = genTestSuite();
//        {
//            assertEquals(3, suite.getErrors());
//            assertEquals(3, suite.getFailures());
//            assertEquals(1, suite.getSkipped());
//            assertEquals("testSuiteGroup", suite.getGroup());
//            assertEquals("testSuiteName", suite.getName());
//            {
//                assertEquals(1, suite.getProperties().size());
//                final com.github.ericdriggs.reportcard.xml.surefire.Properties properties = suite.getProperties().get(0);
//                assertEquals(1, properties.getProperty().size());
//                final com.github.ericdriggs.reportcard.xml.surefire.Property property = properties.getProperty().get(0);
//                assertEquals("foo", property.getName());
//                assertEquals("bar", property.getValue());
//            }
//            assertEquals(8, suite.getTests());
//            assertEquals(new BigDecimal("9.84"), suite.getTime());
//            {
//                List<com.github.ericdriggs.reportcard.xml.surefire.Testcase> testcases = suite.getTestcase();
//                //TODO: assert testcases;
//            }
//        }
////
//        com.github.ericdriggs.reportcard.model.TestSuite modelTestSuite = modelMapper.map(suite, com.github.ericdriggs.reportcard.model.TestSuite.class);
//        {
//            assertEquals(3, modelTestSuite.getError());
//            assertEquals(3, modelTestSuite.getFailure());
//            assertEquals(1, modelTestSuite.getSkipped());
//            assertEquals("testSuiteGroup", modelTestSuite.getGroup());
//
////            assertEquals("testSuiteName", modelTestSuite.getName());
////            {
////                assertEquals(1, modelTestSuite.getProperties().size());
////                final Properties properties = modelTestSuite.getProperties().get(0);
////                assertEquals(1, properties.getProperty().size());
////                final Property property = properties.getProperty().get(0);
////                assertEquals("foo", property.getName());
////                assertEquals("bar", property.getValue());
////            }
//            assertEquals(8, modelTestSuite.getTests());
//            assertEquals(new BigDecimal("9.84"), modelTestSuite.getTime());
//            {
//                List<com.github.ericdriggs.reportcard.model.TestCase> testcases = modelTestSuite.getTestCases();
//                assertEquals(TestStatus.SUCCESS, testcases.get(0).getTestStatus());
//                //TODO: assert testcases;
//            }
//        }
//
////        assertEquals(surefireTestCase.getClassname(), modelTestCase.getClassName());
////        assertEquals(surefireTestCase.getName(), modelTestCase.getName());
////        assertEquals(surefireTestCase.getTime(), modelTestCase.getTime().toPlainString());
////        assertEquals(TestStatus.FAILURE, modelTestCase.getTestStatus());
//    }
//
//    @Test
//    public void testResultTest() {
//
//        Testsuite suite = genTestSuite();
//        List<Testsuite> testsuites = new ArrayList<>();
//        testsuites.add(suite);
//        testsuites.add(suite); //calculations are by value so add same twice is valid
//
////        com.github.ericdriggs.reportcard.model.TestResult modelTestResult = modelMapper.map(testsuites, com.github.ericdriggs.reportcard.model.TestResult.class);
//        com.github.ericdriggs.reportcard.model.TestResult modelTestResult = doFromSurefireToModelTestResult(testsuites);
//        assertEquals(16, modelTestResult.getTests());
//        assertEquals(6, modelTestResult.getError());
//        assertEquals(6, modelTestResult.getFailure());
//        assertEquals(2, modelTestResult.getSkipped());
//
//
//    }
//}
