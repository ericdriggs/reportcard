package io.github.ericdriggs.reportcard.model.converter.junit;

import io.github.ericdriggs.reportcard.model.TestCase;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.github.ericdriggs.reportcard.model.TestSuite;
import io.github.ericdriggs.reportcard.xml.junit.Testcase;
import io.github.ericdriggs.reportcard.xml.junit.Testsuite;
import io.github.ericdriggs.reportcard.xml.junit.Testsuites;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JunitConvertersUtil {

    public final static Converter<Testcase, TestCase> fromJunitToModelTestCase = new AbstractConverter<Testcase, TestCase>() {
        protected TestCase convert(io.github.ericdriggs.reportcard.xml.junit.Testcase source) {
            return doFromJunitToModelTestCase(source);
        }
    };
    
    public final static Converter<Testsuite, TestSuite> fromJunitToModelTestSuite = new AbstractConverter<Testsuite, TestSuite>() {
        protected TestSuite convert(io.github.ericdriggs.reportcard.xml.junit.Testsuite source) {
            return doFromJunitToModelTestSuite(source);
        }
    };

    public final static Converter<Testsuites, TestResult> fromJunitToModelTestResult = new AbstractConverter<Testsuites, TestResult>() {
        protected TestResult convert(io.github.ericdriggs.reportcard.xml.junit.Testsuites source) {
            return doFromJunitToModelTestResult(source);
        }
    };

    public static TestCase doFromJunitToModelTestCase(Testcase source) {
        TestCase modelTestCase = new TestCase();
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

    public static List<TestCase> doFromJunitToModelTestCases(List<io.github.ericdriggs.reportcard.xml.junit.Testcase> source) {
        List<TestCase> testCases = new ArrayList<>();
        for (io.github.ericdriggs.reportcard.xml.junit.Testcase testcase : source) {
            testCases.add(doFromJunitToModelTestCase(testcase));
        }
        return testCases;
    }


    public static List<TestSuite> doFromJunitToModelTestSuites(Testsuites source) {
        List<TestSuite> testSuites = new ArrayList<>();
        for (Testsuite testsuite : source.getTestsuite()) {
            testSuites.add(doFromJunitToModelTestSuite(testsuite));
        }
        return testSuites;
    }

    public static TestSuite doFromJunitToModelTestSuite(Testsuite source) {
        TestSuite modelTestSuite = new TestSuite();
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
        modelTestSuite.setProperties(null);
        modelTestSuite.setTestCases(doFromJunitToModelTestCases(source.getTestcase()));
        modelTestSuite.setTime(source.getTime());
        if (modelTestSuite.getTime() == null) {
            modelTestSuite.setTime(BigDecimal.ZERO);
        }

        return modelTestSuite;
    }

    public static TestResult doFromJunitToModelTestResult(Testsuites sources) {
        TestResult modelTestResult = new TestResult();
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

    public final static ModelMapper modelMapper = new ModelMapper();
    static {
        modelMapper.addConverter(fromJunitToModelTestCase);
        modelMapper.addConverter(fromJunitToModelTestSuite);
        modelMapper.addConverter(fromJunitToModelTestResult);
    }
}
