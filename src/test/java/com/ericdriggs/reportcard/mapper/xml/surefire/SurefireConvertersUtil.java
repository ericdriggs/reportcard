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
import java.util.ArrayList;
import java.util.List;

public class SurefireConvertersUtil {

    public static Converter<Testcase, TestCase> fromSurefireToModelTestCase = new AbstractConverter<>() {
        protected com.ericdriggs.reportcard.model.TestCase convert(com.ericdriggs.reportcard.xml.surefire.Testcase source) {
            return doFromSurefireToModelTestCase(source);
        }
    };

    public static List<TestCase> doFromSurefireToModelTestCases(List<Testcase> source) {
        List<TestCase> testCases = new ArrayList<>();
        for (Testcase testcase : source) {
            testCases.add(doFromSurefireToModelTestCase(testcase));
        }
        return testCases;
    }

    public static TestCase doFromSurefireToModelTestCase(Testcase source) {
        com.ericdriggs.reportcard.model.TestCase modelTestCase = new com.ericdriggs.reportcard.model.TestCase();
        modelTestCase.setName(source.getName());
        modelTestCase.setClassName(source.getClassname());
        modelTestCase.setTime(new BigDecimal(source.getTime()));

        if (source.getSkipped() != null) {
            modelTestCase.setTestStatus(TestStatus.SKIPPED);
        } else if (source.getFailure() != null) {
            modelTestCase.setTestStatus(TestStatus.FAILURE);
        } else if (source.getError() != null) {
            modelTestCase.setTestStatus(TestStatus.ERROR);
        } else {
            modelTestCase.setTestStatus(TestStatus.SUCCESS);
        }
        return modelTestCase;
    }

    public static ModelMapper testCasemodelMapper = new ModelMapper();

    static {
        testCasemodelMapper.addConverter(fromSurefireToModelTestCase);
    }

    public static Converter<Testsuite, TestSuite> fromSurefireToModelTestSuite = new AbstractConverter<>() {
        protected com.ericdriggs.reportcard.model.TestSuite convert(com.ericdriggs.reportcard.xml.surefire.Testsuite source) {
            return doFromSurefireToModelTestSuite(source);
        }
    };

    public static TestSuite doFromSurefireToModelTestSuite(Testsuite source) {
        com.ericdriggs.reportcard.model.TestSuite modelTestSuite = new com.ericdriggs.reportcard.model.TestSuite();
        modelTestSuite.setError(source.getErrors());
        modelTestSuite.setFailure(source.getFailures());
        modelTestSuite.setSkipped(source.getSkipped());
        modelTestSuite.setTests(source.getTests());
        modelTestSuite.setGroup(source.getGroup());
        modelTestSuite.setPackage(null);
        modelTestSuite.setProperties(null); //TODO: support properties;
        modelTestSuite.setTestCases(doFromSurefireToModelTestCases(source.getTestcase())); //FIXME: set test cases from loop
        modelTestSuite.setTime(source.getTime());

        return modelTestSuite;
    }

    public static ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addConverter(fromSurefireToModelTestCase);
        modelMapper.addConverter(fromSurefireToModelTestSuite);
    }
}
