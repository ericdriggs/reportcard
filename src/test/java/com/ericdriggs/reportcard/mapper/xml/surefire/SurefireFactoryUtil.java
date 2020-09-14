//package com.ericdriggs.reportcard.mapper.xml.surefire;
//
//import com.ericdriggs.reportcard.model.TestStatus;
//import com.ericdriggs.reportcard.xml.surefire.Testcase;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//import java.util.Random;
//
//public class SurefireFactoryUtil {
//    private SurefireFactoryUtil() {
//        //static only
//    }
//
//    private final static Random random = new Random();
//
//    public static Testcase testcase(TestStatus testStatus) {
//
//        Long randLong = random.nextLong();
//        final String className = "classname_" + randLong;
//        final String name = "name_" + randLong;
//        final String time = "1.23";
//
//
//        final String surefireErrorMessage = "surefireErrorMessage_" + randLong;
//        final String surefireErrorType = "surefireErrorType_" + randLong;
//        final String surefireErrorValue = "surefireErrorType_" + randLong;
//
//        final String surefireFailureMessage = "surefireFailureMessage_" + randLong;
//        final String surefireFailureType = "surefireFailureType_" + randLong;
//        final String surefireFailureValue = "surefireFailureValue_" + randLong;
//
//        Testcase surefireTestCase = new Testcase();
//        surefireTestCase.setClassname(className);
//        surefireTestCase.setName(name);
//        surefireTestCase.setTime(time);
//
//        if (testStatus.equals(TestStatus.SKIPPED)) {
//            final Object skipped = new Object();
//            surefireTestCase.setSkipped(skipped);
//        }
//
//        if (testStatus.equals(TestStatus.FAILURE)){
//            com.ericdriggs.reportcard.xml.surefire.Failure surefireFailure = new com.ericdriggs.reportcard.xml.surefire.Failure();
//            surefireFailure.setMessage(surefireFailureMessage);
//            surefireFailure.setType(surefireFailureType);
//            surefireFailure.setValue(surefireFailureValue);
//            surefireTestCase.setFailure(Collections.singletonList(surefireFailure));
//        }
//
//        if (testStatus.equals(TestStatus.ERROR)){
//            com.ericdriggs.reportcard.xml.surefire.Error surefireError = new com.ericdriggs.reportcard.xml.surefire.Error();
//            surefireError.setMessage(surefireErrorMessage);
//            surefireError.setType(surefireErrorType);
//            surefireError.setValue(surefireErrorValue);
//            surefireTestCase.setError(Collections.singletonList(surefireError));
//        }
//
//        return surefireTestCase;
//    }
//}
