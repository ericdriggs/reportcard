package com.ericdriggs.reportcard.mapper.xml.surefire;

import com.ericdriggs.reportcard.model.TestCase;
import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.model.TestSuite;
import com.ericdriggs.reportcard.xml.surefire.Testcase;
import com.ericdriggs.reportcard.xml.surefire.Testsuite;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

public class SurefireConvertersUtil {

    public static Converter<Testcase, TestCase> fromSurefireToModelTestCase = new AbstractConverter<>() {
        protected com.ericdriggs.reportcard.model.TestCase convert(com.ericdriggs.reportcard.xml.surefire.Testcase source) {
            com.ericdriggs.reportcard.model.TestCase modelTestCase = new com.ericdriggs.reportcard.model.TestCase();
            modelTestCase.setName(source.getName());
            modelTestCase.setClassName(source.getClassname());
            modelTestCase.setTime(new BigDecimal(source.getTime()));

            if (source.getSkipped() != null) {
                modelTestCase.setTestStatus(TestStatus.SKIPPED);
            }
            else if (source.getFailure() != null) {
                modelTestCase.setTestStatus(TestStatus.FAILURE);
            }
            else if (source.getError() != null) {
                modelTestCase.setTestStatus(TestStatus.ERROR);
            }
            else {
                modelTestCase.setTestStatus(TestStatus.SUCCESS);
            }
            return modelTestCase;
        }
    };

    public static Converter<Testsuite, TestSuite> fromSurefireToModelTestSuite = new AbstractConverter<>() {
        protected com.ericdriggs.reportcard.model.TestSuite convert(com.ericdriggs.reportcard.xml.surefire.Testsuite source) {
            com.ericdriggs.reportcard.model.TestSuite modelTestSuite = new com.ericdriggs.reportcard.model.TestSuite();
//            modelTestSuite.setTestCases(); //FIXME: set test cases from loop
            modelTestSuite.setPackage(null);
            modelTestSuite.setError(source.getErrors());
            modelTestSuite.setFailure(source.getFailures());
            modelTestSuite.setSkipped(source.getSkipped());
            modelTestSuite.setGroup(source.getGroup());
            modelTestSuite.setProperties(null); //TODO: support properties;
            modelTestSuite.setTime(source.getTime());
//            modelTestSuite.setIsSuccess(modelTestSuite.calcIsSuccess());
//            modelTestSuite.setName(source.getName());
//            modelTestCase.setClassName(source.getClassname());
//            modelTestCase.setTime(new BigDecimal(source.getTime()));
//
//            if (source.getSkipped() != null) {
//                modelTestCase.setTestStatus(TestStatus.SKIPPED);
//            }
//            else if (source.getFailure() != null) {
//                modelTestCase.setTestStatus(TestStatus.FAILURE);
//            }
//            else if (source.getError() != null) {
//                modelTestCase.setTestStatus(TestStatus.ERROR);
//            }
//            else {
//                modelTestCase.setTestStatus(TestStatus.SUCCESS);
//            }
            return modelTestSuite;
        }
    };

    public static ModelMapper modelMapper = new ModelMapper();
    static {
        modelMapper.addConverter(fromSurefireToModelTestCase);
    }
}
