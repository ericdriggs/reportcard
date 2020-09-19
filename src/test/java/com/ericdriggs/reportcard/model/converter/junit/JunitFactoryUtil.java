package com.ericdriggs.reportcard.model.converter.junit;

import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.model.TestStatusType;
import com.ericdriggs.reportcard.xml.junit.*;
import com.ericdriggs.reportcard.xml.junit.Error;

import javax.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.util.*;

public class JunitFactoryUtil {
    private JunitFactoryUtil() {
        //static only
    }

    private final static Random random = new Random();
    private final static ObjectFactory objectFactory = new ObjectFactory();

    public static Testsuite newTestSuite(List<TestStatus> testStatuses) {

        Map<TestStatusType, Integer> testStatusTypeCount = new HashMap<>();
        for (TestStatusType testStatusType : TestStatusType.values()) {
            testStatusTypeCount.put(testStatusType, 0);
        }

        BigDecimal testcaseTime = BigDecimal.ZERO;
        Integer testcaseCount = 0;

        List<com.ericdriggs.reportcard.xml.junit.Testcase> testcases = new ArrayList<>();
        for (TestStatus testStatus : testStatuses) {
            com.ericdriggs.reportcard.xml.junit.Testcase testcase = newTestCase(testStatus);
            testcases.add(testcase);
            TestStatusType testStatusType = testStatus.getTestStatusType();
            testStatusTypeCount.put(testStatusType, testStatusTypeCount.get(testStatusType) + 1);
            testcaseTime = testcaseTime.add(testcase.getTime());
            testcaseCount++;
        }

        final long randLong = random.nextLong();
        final String testSuitePackage = "com.foo.bar";
        final String testSuiteName = "testSuiteName";
        final List<com.ericdriggs.reportcard.xml.junit.Properties> properties = newProperties(Collections.singletonMap("foo", "bar"));

        Testsuite testsuite = new Testsuite();
        testsuite.setErrors(testStatusTypeCount.get(TestStatusType.ERROR));
        testsuite.setFailures(testStatusTypeCount.get(TestStatusType.FAILURE));
        testsuite.set_package(testSuitePackage);
        testsuite.setName(testSuiteName);
        testsuite.setSkipped(testStatusTypeCount.get(TestStatusType.SKIPPED));
        testsuite.setTestcase(testcases);
        testsuite.setTime(testcaseTime);
        testsuite.setTests(testcaseCount);

        return testsuite;
    }

    public static List<com.ericdriggs.reportcard.xml.junit.Properties> newProperties(Map<String, String> propertyMap) {
        com.ericdriggs.reportcard.xml.junit.Properties properties = new com.ericdriggs.reportcard.xml.junit.Properties();
        List<Property> propertyList = new ArrayList<>();
        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            Property property = new Property();
            property.setName(entry.getKey());
            property.setValue(entry.getValue());
            propertyList.add(property);
        }
        properties.setProperty(propertyList);
        return Collections.singletonList(properties);
    }

    public static com.ericdriggs.reportcard.xml.junit.Testcase newTestCase(TestStatus testStatus) {

        final long randLong = random.nextLong();
        final String className = "classname";
        final String name = "name";
        final BigDecimal time = new BigDecimal("1.23");

        final String junitSkippedMessage = "junitSkippedMessage";
        final String junitSkippedValue = "junitSkippedValue";


        final String junitErrorMessage = "junitErrorMessage";
        final String junitErrorType = "junitErrorType";
        final String junitErrorValue = "junitErrorType";

        final String junitFailureMessage = "junitFailureMessage";
        final String junitFailureType = "junitFailureType";
        final String junitFailureValue = "junitFailureType";

        final String stackTrace = "stacktrace";
        final String systemError = "system error";
        final String systemOut = "system out";

        com.ericdriggs.reportcard.xml.junit.Testcase junitTestCase = new Testcase();
        junitTestCase.setClassname(className);
        junitTestCase.setName(name);
        junitTestCase.setTime(time);

        if (testStatus.equals(TestStatus.SUCCESS)) {
            //NOOP
        } else if (testStatus.equals(TestStatus.SKIPPED)) {
            Object junitSkipped = new Object();
            junitTestCase.setSkipped(junitSkipped);
        } else if (testStatus.equals(TestStatus.FAILURE)) {
            Failure elem = new Failure();
            elem.setMessage(junitFailureMessage);
            elem.setType(junitFailureType);
            elem.setValue(junitFailureValue);
            junitTestCase.setFailure(elem);
        } else if (testStatus.equals(TestStatus.ERROR)) {
            com.ericdriggs.reportcard.xml.junit.Error elem = new com.ericdriggs.reportcard.xml.junit.Error();
            elem.setMessage(junitErrorMessage);
            elem.setType(junitErrorType);
            elem.setValue(junitErrorValue);
            junitTestCase.setError(elem);
        } else {
            throw new UnsupportedOperationException("FIXME: handle TestStatus." + testStatus.name());
        }
        return junitTestCase;
    }
}
