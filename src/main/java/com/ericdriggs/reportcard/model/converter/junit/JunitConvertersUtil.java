package com.ericdriggs.reportcard.model.converter.junit;

import com.ericdriggs.reportcard.model.TestCase;
import com.ericdriggs.reportcard.model.TestResult;
import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.model.TestSuite;
import com.ericdriggs.reportcard.xml.junit.Testcase;
import com.ericdriggs.reportcard.xml.junit.Testsuite;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JunitConvertersUtil {

    public static Converter<Testcase, TestCase> fromJunitToModelTestCase = new AbstractConverter<>() {
        protected com.ericdriggs.reportcard.model.TestCase convert(com.ericdriggs.reportcard.xml.junit.Testcase source) {
            return doFromJunitToModelTestCase(source);
        }
    };

    public static TestCase doFromJunitToModelTestCase(Testcase source) {
        com.ericdriggs.reportcard.model.TestCase modelTestCase = new com.ericdriggs.reportcard.model.TestCase();
        modelTestCase.setName(source.getName());
        modelTestCase.setClassName(source.getClassname());
        modelTestCase.setTime(source.getTime());

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

    public static List<TestCase> doFromJunitToModelTestCases(List<com.ericdriggs.reportcard.xml.junit.Testcase> source) {
        List<TestCase> testCases = new ArrayList<>();
        for (com.ericdriggs.reportcard.xml.junit.Testcase testcase : source) {
            testCases.add(doFromJunitToModelTestCase(testcase));
        }
        return testCases;
    }


    public static List<TestSuite> doFromJunitToModelTestSuites(Collection<Testsuite> source) {
        List<TestSuite> testSuites = new ArrayList<>();
        for (Testsuite testsuite : source) {
            testSuites.add(doFromJunitToModelTestSuite(testsuite));
        }
        return testSuites;
    }

    public static TestSuite doFromJunitToModelTestSuite(Testsuite source) {
        com.ericdriggs.reportcard.model.TestSuite modelTestSuite = new com.ericdriggs.reportcard.model.TestSuite();
        modelTestSuite.setError(source.getErrors());
        if (modelTestSuite.getError() == null) {
            modelTestSuite.setError(0);
        }
        modelTestSuite.setFailure(source.getFailures());
        if (modelTestSuite.getFailure() == null) {
            modelTestSuite.setFailure(0);
        }
        modelTestSuite.setSkipped(source.getSkipped());
        if (modelTestSuite.getSkipped() == null) {
            modelTestSuite.setSkipped(0);
        }
        modelTestSuite.setTests(source.getTests());
        if (modelTestSuite.getTests() == null) {
            modelTestSuite.setTests(0);
        }
        modelTestSuite.setSkipped(source.getSkipped());
        if (modelTestSuite.getSkipped() == null) {
            modelTestSuite.setSkipped(0);
        }
        modelTestSuite.setPackage(source.get_package());
        modelTestSuite.setPackage(null);
        modelTestSuite.setProperties(null); //TODO: support properties;
        modelTestSuite.setTestCases(doFromJunitToModelTestCases(source.getTestcase()));
        modelTestSuite.setTime(source.getTime());
        if (modelTestSuite.getTime() == null) {
            modelTestSuite.setTime(BigDecimal.ZERO);
        }

        return modelTestSuite;
    }

    public static TestResult doFromJunitToModelTestResult(Collection<Testsuite> sources) {
        com.ericdriggs.reportcard.model.TestResult modelTestResult = new com.ericdriggs.reportcard.model.TestResult();
        modelTestResult.setTestSuites(doFromJunitToModelTestSuites(sources));
        modelTestResult.setTests(0);
        modelTestResult.setSkipped(0);
        modelTestResult.setFailure(0);
        modelTestResult.setError(0);
        modelTestResult.setTime(BigDecimal.ZERO);

        for (TestSuite testSuite : modelTestResult.getTestSuites()) {
            modelTestResult.setTime(modelTestResult.getTime().add(testSuite.getTime()));
            modelTestResult.setTests(modelTestResult.getTests() + testSuite.getTests());
            modelTestResult.setSkipped(modelTestResult.getSkipped() + testSuite.getSkipped());
            modelTestResult.setFailure(modelTestResult.getFailure() + testSuite.getFailure());
            modelTestResult.setError(modelTestResult.getError() + testSuite.getError());
        }
        return modelTestResult;
    }

    public static ModelMapper modelMapper = new ModelMapper();
    static {
        modelMapper.addConverter(fromJunitToModelTestCase);
    }
}
