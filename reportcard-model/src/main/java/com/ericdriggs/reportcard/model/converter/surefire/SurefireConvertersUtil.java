package com.ericdriggs.reportcard.model.converter.surefire;

import com.ericdriggs.reportcard.model.TestCase;
import com.ericdriggs.reportcard.model.TestResult;
import com.ericdriggs.reportcard.model.TestStatus;
import com.ericdriggs.reportcard.model.TestSuite;
import com.ericdriggs.reportcard.xml.surefire.Testcase;
import com.ericdriggs.reportcard.xml.surefire.Testsuite;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SurefireConvertersUtil {

    public final static Converter<Testcase, TestCase> fromSurefireToModelTestCase = new AbstractConverter<Testcase, TestCase>() {
        protected TestCase convert(com.ericdriggs.reportcard.xml.surefire.Testcase source) {
            return doFromSurefireToModelTestCase(source);
        }
    };

    public final static Converter<Testsuite, TestSuite> fromSurefireToModelTestSuite = new AbstractConverter<Testsuite, TestSuite>() {
        protected TestSuite convert(com.ericdriggs.reportcard.xml.surefire.Testsuite source) {
            return doFromSurefireToModelTestSuite(source);
        }
    };

    public final static Converter<Collection<Testsuite>, TestResult> fromSurefireToModelTestResult = new AbstractConverter<Collection<Testsuite>, TestResult>() {
        protected TestResult convert(Collection<Testsuite> source) {
            return doFromSurefireToModelTestResult(source);
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
        TestCase modelTestCase = new TestCase();
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

    public static List<TestSuite> doFromSurefireToModelTestSuites(Collection<Testsuite> source) {
        List<TestSuite> testSuites = new ArrayList<>();
        for (Testsuite testsuite : source) {
            testSuites.add(doFromSurefireToModelTestSuite(testsuite));
        }
        return testSuites;
    }

    public static TestSuite doFromSurefireToModelTestSuite(Testsuite source) {
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
        modelTestSuite.setGroup(source.getGroup());
        modelTestSuite.setPackage(null);
        modelTestSuite.setProperties(null); //TODO: support properties;
        modelTestSuite.setTestCases(doFromSurefireToModelTestCases(source.getTestcase()));
        modelTestSuite.setTime(source.getTime());
        if (modelTestSuite.getTime() == null) {
            modelTestSuite.setTime(BigDecimal.ZERO);
        }

        return modelTestSuite;
    }

    public static TestResult doFromSurefireToModelTestResult(Collection<Testsuite> sources) {
        TestResult modelTestResult = new TestResult();
        modelTestResult.setTestSuites(doFromSurefireToModelTestSuites(sources));
        modelTestResult.setTests(0);
        modelTestResult.setSkipped(0);
        modelTestResult.setFailure(0);
        modelTestResult.setError(0);
        modelTestResult.setTime(BigDecimal.ZERO);

        boolean hasSkip = false;
        for (TestSuite testSuite : modelTestResult.getTestSuites()) {
            modelTestResult.setTime(modelTestResult.getTime().add(testSuite.getTime()));
            modelTestResult.setTests(modelTestResult.getTests() + testSuite.getTests());
            modelTestResult.setSkipped(modelTestResult.getSkipped() + testSuite.getSkipped());
            modelTestResult.setFailure(modelTestResult.getFailure() + testSuite.getFailure());
            modelTestResult.setError(modelTestResult.getError() + testSuite.getError());
        }

        if (modelTestResult.getSkipped() > 0) {
            modelTestResult.setHasSkip(true);
        }

        if (modelTestResult.getFailure() > 0 || modelTestResult.getError() > 0) {
            modelTestResult.setIsSuccess(false);
        }

        return modelTestResult;
    }

    public final static ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addConverter(fromSurefireToModelTestCase);
        modelMapper.addConverter(fromSurefireToModelTestSuite);
        modelMapper.addConverter(fromSurefireToModelTestResult);
    }
}
