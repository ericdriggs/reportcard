package com.ericdriggs.reportcard.model.converter.junit;

import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.xml.junit.Testcase;
import java.math.BigDecimal;
import java.util.Random;

public class JunitFactoryUtil {
    private JunitFactoryUtil() {
        //static only
    }

    private final static Random random = new Random();

    public static Testcase testcase(TestStatus testStatus) {

        Long randLong = random.nextLong();
        final String className = "classname_" + randLong;
        final String name = "name_" + randLong;
        final BigDecimal time = new BigDecimal("1.23");


        final String junitErrorMessage = "junitErrorMessage_" + randLong;
        final String junitErrorType = "junitErrorType_" + randLong;
        final String junitErrorValue = "junitErrorType_" + randLong;

        final String junitFailureMessage = "junitFailureMessage_" + randLong;
        final String junitFailureType = "junitFailureType_" + randLong;
        final String junitFailureValue = "junitFailureValue_" + randLong;

        com.ericdriggs.reportcard.xml.junit.Testcase junitTestCase = new com.ericdriggs.reportcard.xml.junit.Testcase();
        junitTestCase.setClassname(className);
        junitTestCase.setName(name);
        junitTestCase.setTime(time);

        if (testStatus.equals(TestStatus.SUCCESS)){
            //NOOP
        }
        else if (testStatus.equals(TestStatus.SKIPPED)) {
            final Object skipped = new Object();
            junitTestCase.setSkipped(skipped);
        }

        else if (testStatus.equals(TestStatus.FAILURE)){
            com.ericdriggs.reportcard.xml.junit.Failure junitFailure = new com.ericdriggs.reportcard.xml.junit.Failure();
            junitFailure.setMessage(junitFailureMessage);
            junitFailure.setType(junitFailureType);
            junitFailure.setValue(junitFailureValue);
            junitTestCase.setFailure(junitFailure);
        }

        else if (testStatus.equals(TestStatus.ERROR)){
            com.ericdriggs.reportcard.xml.junit.Error junitError = new com.ericdriggs.reportcard.xml.junit.Error();
            junitError.setMessage(junitErrorMessage);
            junitError.setType(junitErrorType);
            junitError.setValue(junitErrorValue);
            junitTestCase.setError(junitError);
        }

        return junitTestCase;
    }
}
