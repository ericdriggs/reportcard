package com.ericdriggs.reportcard.mapper.xml.surefire;

import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.model.TestStatusType;
import com.ericdriggs.reportcard.xml.surefire.*;
import com.ericdriggs.reportcard.xml.surefire.Error;
import com.ericdriggs.reportcard.xml.surefire.Properties;

import javax.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.util.*;

public class SurefireFactoryUtil {
    private SurefireFactoryUtil() {
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

        List<Testcase> testcases = new ArrayList<>();
        for (TestStatus testStatus : testStatuses) {
            Testcase testcase = newTestCase(testStatus);
            testcases.add(testcase);
            TestStatusType testStatusType = testStatus.getTestStatusType();
            testStatusTypeCount.put(testStatusType, testStatusTypeCount.get(testStatusType) + 1);
            testcaseTime = testcaseTime.add(new BigDecimal(testcase.getTime()));
            testcaseCount++;
        }

        final long randLong = random.nextLong();
        final String testSuiteGroup = "testSuiteGroup";
        final String testSuiteName = "testSuiteName";
        final List<Properties> properties = newProperties(Collections.singletonMap("foo", "bar"));

        Testsuite testsuite = new Testsuite();
        testsuite.setErrors(testStatusTypeCount.get(TestStatusType.ERROR));
        testsuite.setFailures(testStatusTypeCount.get(TestStatusType.FAILURE));
        testsuite.setGroup(testSuiteGroup);
        testsuite.setName(testSuiteName);
        testsuite.setProperties(properties);
        testsuite.setSkipped(testStatusTypeCount.get(TestStatusType.SKIPPED));
        testsuite.setTestcase(testcases);
        testsuite.setTime(testcaseTime);
        testsuite.setTests(testcaseCount);

        return testsuite;
    }

    public static List<Properties> newProperties(Map<String, String> propertyMap) {
        Properties properties = new Properties();
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

    public static Testcase newTestCase(TestStatus testStatus) {

        final long randLong = random.nextLong();
        final String className = "classname";
        final String name = "name";
        final String time = "1.23";

        final String surefireSkippedMessage = "surefireSkippedMessage";
        final String surefireSkippedValue = "surefireSkippedValue";


        final String surefireErrorMessage = "surefireErrorMessage";
        final String surefireErrorType = "surefireErrorType";
        final String surefireErrorValue = "surefireErrorType";

        final String surefireFailureMessage = "surefireFailureMessage";
        final String surefireFailureType = "surefireFailureType";
        final String surefireFailureValue = "surefireFailureType";

        final String stackTrace = "stacktrace";
        final String systemError = "system error";
        final String systemOut = "system out";

        Testcase surefireTestCase = new Testcase();
        surefireTestCase.setClassname(className);
        surefireTestCase.setName(name);
        surefireTestCase.setTime(time);

         if (testStatus.equals(TestStatus.SUCCESS)) {
            //NOOP
        }
        else if (testStatus.equals(TestStatus.SKIPPED)) {
            Skipped surefireSkipped = new Skipped();
            surefireSkipped.setMessage(surefireSkippedMessage);
            surefireSkipped.setValue(surefireSkippedValue);
            JAXBElement<Skipped> skippedJAXBElement =
                    objectFactory.createTestsuiteTestcaseSkipped(surefireSkipped);
            surefireTestCase.setSkipped(skippedJAXBElement);
        } else if (testStatus.equals(TestStatus.FAILURE)) {
            Failure elem = new Failure();
            elem.setMessage(surefireFailureMessage);
            elem.setType(surefireFailureType);
            elem.setValue(surefireFailureValue);
            surefireTestCase.setFailure(Collections.singletonList(elem));
        } else if (testStatus.equals(TestStatus.ERROR)) {
            Error elem = new Error();
            elem.setMessage(surefireErrorMessage);
            elem.setType(surefireErrorType);
            elem.setValue(surefireErrorValue);
            JAXBElement<Error> errorJAXBElement =
                    objectFactory.createTestsuiteTestcaseError(elem);
            surefireTestCase.setError(errorJAXBElement);
        } else if (testStatus.equals(TestStatus.FLAKY_ERROR)) {
            FlakyError elem = new FlakyError();
            elem.setMessage(surefireErrorMessage);
            elem.setType(surefireErrorType);
            elem.setStackTrace(stackTrace);
            elem.setSystemErr(systemError);
            elem.setSystemOut(systemOut);
            surefireTestCase.setFlakyError(Collections.singletonList(elem));
        } else if (testStatus.equals(TestStatus.FLAKY_FAILURE)) {
            FlakyFailure elem = new FlakyFailure();
            elem.setMessage(surefireFailureMessage);
            elem.setType(surefireFailureType);
            elem.setStackTrace(stackTrace);
            elem.setSystemErr(systemError);
            elem.setSystemOut(systemOut);
            surefireTestCase.setFlakyFailure(Collections.singletonList(elem));
        } else if (testStatus.equals(TestStatus.RERUN_ERROR)) {
            RerunError elem = new RerunError();
            elem.setMessage(surefireErrorMessage);
            elem.setType(surefireErrorType);
            elem.setStackTrace(stackTrace);
            elem.setSystemErr(systemError);
            elem.setSystemOut(systemOut);
            surefireTestCase.setRerunError(Collections.singletonList(elem));
        } else if (testStatus.equals(TestStatus.RERUN_FAILURE)) {
            FlakyFailure elem = new FlakyFailure();
            elem.setMessage(surefireFailureMessage);
            elem.setType(surefireFailureType);
            elem.setStackTrace(stackTrace);
            elem.setSystemErr(systemError);
            elem.setSystemOut(systemOut);
            surefireTestCase.setFlakyFailure(Collections.singletonList(elem));
        } else {
            throw new UnsupportedOperationException("FIXME: handle TestStatus." + testStatus.name());
        }

        return surefireTestCase;
    }
}
