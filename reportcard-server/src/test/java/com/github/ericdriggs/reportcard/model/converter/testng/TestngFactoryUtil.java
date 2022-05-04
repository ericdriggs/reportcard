//package com.github.ericdriggs.reportcard.model.converter.testng;
//
//import com.github.ericdriggs.reportcard.model.TestStatus;
//import com.github.ericdriggs.reportcard.model.TestStatusType;
//import com.github.ericdriggs.reportcard.xml.testng.suite.Error;
//import com.github.ericdriggs.reportcard.xml.testng.suite.Properties;
//import com.github.ericdriggs.reportcard.xml.testng.suite.*;
//
//import javax.xml.bind.JAXBElement;
//import java.math.BigDecimal;
//import java.util.*;
//
//public class TestngFactoryUtil {
//    private TestngFactoryUtil() {
//        //static only
//    }
//
//    private final static Random random = new Random();
//    private final static ObjectFactory objectFactory = new ObjectFactory();
//
//    public static Testsuite newTestSuite(List<TestStatus> testStatuses) {
//
//        Map<TestStatusType, Integer> testStatusTypeCount = new HashMap<>();
//        for (TestStatusType testStatusType : TestStatusType.values()) {
//            testStatusTypeCount.put(testStatusType, 0);
//        }
//
//        BigDecimal testcaseTime = BigDecimal.ZERO;
//        Integer testcaseCount = 0;
//
//        List<Testcase> testcases = new ArrayList<>();
//        for (TestStatus testStatus : testStatuses) {
//            Testcase testcase = newTestCase(testStatus);
//            testcases.add(testcase);
//            TestStatusType testStatusType = testStatus.getTestStatusType();
//            testStatusTypeCount.put(testStatusType, testStatusTypeCount.get(testStatusType) + 1);
//            testcaseTime = testcaseTime.add(new BigDecimal(testcase.getTime()));
//            testcaseCount++;
//        }
//
//        final long randLong = random.nextLong();
//        final String testSuiteGroup = "testSuiteGroup";
//        final String testSuiteName = "testSuiteName";
//        final List<Properties> properties = newProperties(Collections.singletonMap("foo", "bar"));
//
//        Testsuite testsuite = new Testsuite();
//        testsuite.setErrors(testStatusTypeCount.get(TestStatusType.ERROR));
//        testsuite.setFailures(testStatusTypeCount.get(TestStatusType.FAILURE));
//        testsuite.setGroup(testSuiteGroup);
//        testsuite.setName(testSuiteName);
//        testsuite.setProperties(properties);
//        testsuite.setSkipped(testStatusTypeCount.get(TestStatusType.SKIPPED));
//        testsuite.setTestcase(testcases);
//        testsuite.setTime(testcaseTime);
//        testsuite.setTests(testcaseCount);
//
//        return testsuite;
//    }
//
//    public static List<Properties> newProperties(Map<String, String> propertyMap) {
//        Properties properties = new Properties();
//        List<Property> propertyList = new ArrayList<>();
//        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
//            Property property = new Property();
//            property.setName(entry.getKey());
//            property.setValue(entry.getValue());
//            propertyList.add(property);
//        }
//        properties.setProperty(propertyList);
//        return Collections.singletonList(properties);
//    }
//
//    public static Testcase newTestCase(TestStatus testStatus) {
//
//        final long randLong = random.nextLong();
//        final String className = "classname";
//        final String name = "name";
//        final String time = "1.23";
//
//        final String testngSkippedMessage = "testngSkippedMessage";
//        final String testngSkippedValue = "testngSkippedValue";
//
//
//        final String testngErrorMessage = "testngErrorMessage";
//        final String testngErrorType = "testngErrorType";
//        final String testngErrorValue = "testngErrorType";
//
//        final String testngFailureMessage = "testngFailureMessage";
//        final String testngFailureType = "testngFailureType";
//        final String testngFailureValue = "testngFailureType";
//
//        final String stackTrace = "stacktrace";
//        final String systemError = "system error";
//        final String systemOut = "system out";
//
//        Testcase testngTestCase = new Testcase();
//        testngTestCase.setClassname(className);
//        testngTestCase.setName(name);
//        testngTestCase.setTime(time);
//
//         if (testStatus.equals(TestStatus.SUCCESS)) {
//            //NOOP
//        }
//        else if (testStatus.equals(TestStatus.SKIPPED)) {
//            Skipped testngSkipped = new Skipped();
//            testngSkipped.setMessage(testngSkippedMessage);
//            testngSkipped.setValue(testngSkippedValue);
//            JAXBElement<Skipped> skippedJAXBElement =
//                    objectFactory.createTestsuiteTestcaseSkipped(testngSkipped);
//            testngTestCase.setSkipped(skippedJAXBElement);
//        } else if (testStatus.equals(TestStatus.FAILURE)) {
//            Failure elem = new Failure();
//            elem.setMessage(testngFailureMessage);
//            elem.setType(testngFailureType);
//            elem.setValue(testngFailureValue);
//            testngTestCase.setFailure(Collections.singletonList(elem));
//        } else if (testStatus.equals(TestStatus.ERROR)) {
//            Error elem = new Error();
//            elem.setMessage(testngErrorMessage);
//            elem.setType(testngErrorType);
//            elem.setValue(testngErrorValue);
//            JAXBElement<Error> errorJAXBElement =
//                    objectFactory.createTestsuiteTestcaseError(elem);
//            testngTestCase.setError(errorJAXBElement);
//        } else if (testStatus.equals(TestStatus.FLAKY_ERROR)) {
//            FlakyError elem = new FlakyError();
//            elem.setMessage(testngErrorMessage);
//            elem.setType(testngErrorType);
//            elem.setStackTrace(stackTrace);
//            elem.setSystemErr(systemError);
//            elem.setSystemOut(systemOut);
//            testngTestCase.setFlakyError(Collections.singletonList(elem));
//        } else if (testStatus.equals(TestStatus.FLAKY_FAILURE)) {
//            FlakyFailure elem = new FlakyFailure();
//            elem.setMessage(testngFailureMessage);
//            elem.setType(testngFailureType);
//            elem.setStackTrace(stackTrace);
//            elem.setSystemErr(systemError);
//            elem.setSystemOut(systemOut);
//            testngTestCase.setFlakyFailure(Collections.singletonList(elem));
//        } else if (testStatus.equals(TestStatus.RERUN_ERROR)) {
//            RerunError elem = new RerunError();
//            elem.setMessage(testngErrorMessage);
//            elem.setType(testngErrorType);
//            elem.setStackTrace(stackTrace);
//            elem.setSystemErr(systemError);
//            elem.setSystemOut(systemOut);
//            testngTestCase.setRerunError(Collections.singletonList(elem));
//        } else if (testStatus.equals(TestStatus.RERUN_FAILURE)) {
//            FlakyFailure elem = new FlakyFailure();
//            elem.setMessage(testngFailureMessage);
//            elem.setType(testngFailureType);
//            elem.setStackTrace(stackTrace);
//            elem.setSystemErr(systemError);
//            elem.setSystemOut(systemOut);
//            testngTestCase.setFlakyFailure(Collections.singletonList(elem));
//        } else {
//            throw new UnsupportedOperationException("FIXME: handle TestStatus." + testStatus.name());
//        }
//
//        return testngTestCase;
//    }
//}
