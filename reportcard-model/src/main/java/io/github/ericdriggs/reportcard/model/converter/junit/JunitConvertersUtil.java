package io.github.ericdriggs.reportcard.model.converter.junit;

import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.junit.JunitParserUtil;
import io.github.ericdriggs.reportcard.xml.junit.Testcase;
import io.github.ericdriggs.reportcard.xml.junit.Testsuite;
import io.github.ericdriggs.reportcard.xml.junit.Testsuites;
import io.github.ericdriggs.reportcard.xml.surefire.HasValueMessageTypeSurefire;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JunitConvertersUtil {

    public final static Converter<Testcase, TestCaseModel> fromJunitToModelTestCase = new AbstractConverter<Testcase, TestCaseModel>() {
        protected TestCaseModel convert(io.github.ericdriggs.reportcard.xml.junit.Testcase source) {
            return doFromJunitToModelTestCase(source);
        }
    };
    
    public final static Converter<Testsuite, TestSuiteModel> fromJunitToModelTestSuite = new AbstractConverter<Testsuite, TestSuiteModel>() {
        protected TestSuiteModel convert(io.github.ericdriggs.reportcard.xml.junit.Testsuite source) {
            return doFromJunitToModelTestSuite(source);
        }
    };

    public final static Converter<Testsuites, TestResultModel> fromJunitToModelTestResult = new AbstractConverter<Testsuites, TestResultModel>() {
        protected TestResultModel convert(io.github.ericdriggs.reportcard.xml.junit.Testsuites source) {
            return doFromJunitToModelTestResult(source);
        }
    };

    public static List<TestSuiteModel> fromXmlContents(String testXmlContent) {
        Testsuites testsuite = JunitParserUtil.parseTestSuites(testXmlContent);
        return doFromJunitToModelTestSuites(testsuite);
    }

    public static TestCaseModel doFromJunitToModelTestCase(Testcase source) {
        TestCaseModel modelTestCase = new TestCaseModel();
        modelTestCase.setName(source.getName());
        modelTestCase.setClassName(source.getClassname());
        modelTestCase.setTime(source.getTime());
        modelTestCase.setAssertions(source.getAssertions());
        modelTestCase.setSystemOut(source.getSystemOut());
        modelTestCase.setSystemErr(source.getSystemErr());


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

    public static List<TestCaseFaultModel> getTestCaseFaults(Testcase source) {
        List<TestCaseFaultModel> testCaseFaults = new ArrayList<>();
        testCaseFaults.addAll(getTestCaseFaults(List.of(source.getError()), FaultContext.ERROR));
        testCaseFaults.addAll(getTestCaseFaults(List.of(source.getFailure()), FaultContext.FAILURE));
        return testCaseFaults;
    }

    public static List<TestCaseFaultModel> getTestCaseFaults(List<?> faults, FaultContext faultContext) {
        List<TestCaseFaultModel> testCaseFaults = new ArrayList<>();
        if (!IsEmptyUtil.isCollectionEmpty(faults)) {
            for (Object o : faults) {
                if (o instanceof HasValueMessageTypeSurefire hasValueMessageType) {
                    testCaseFaults.add(getTestCaseFault(hasValueMessageType, faultContext));
                }
            }
        }
        return testCaseFaults;
    }

    public static TestCaseFaultModel getTestCaseFault(HasValueMessageTypeSurefire fault, FaultContext faultContext) {

        TestCaseFaultModel testCaseFault = new TestCaseFaultModel();
        testCaseFault.setFaultContextFk(faultContext.getFaultContextId())
                     .setMessage(fault.getMessage())
                     .setType(fault.getType())
                     .setValue(fault.getValue());
        return testCaseFault;
    }

    public static List<TestCaseModel> doFromJunitToModelTestCases(List<io.github.ericdriggs.reportcard.xml.junit.Testcase> source) {
        List<TestCaseModel> testCases = new ArrayList<>();
        for (io.github.ericdriggs.reportcard.xml.junit.Testcase testcase : source) {
            testCases.add(doFromJunitToModelTestCase(testcase));
        }
        return testCases;
    }


    public static List<TestSuiteModel> doFromJunitToModelTestSuites(Testsuites source) {
        List<TestSuiteModel> testSuites = new ArrayList<>();
        for (Testsuite testsuite : source.getTestsuite()) {
            testSuites.add(doFromJunitToModelTestSuite(testsuite));
        }
        return testSuites;
    }

    public static TestSuiteModel doFromJunitToModelTestSuite(Testsuite source) {
        TestSuiteModel modelTestSuite = new TestSuiteModel();
        modelTestSuite.setSystemErr(source.getSystemErr());
        modelTestSuite.setSystemOut(source.getSystemOut());
        modelTestSuite.setError(source.getErrors());
        modelTestSuite.setName(source.getName());
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
        modelTestSuite.setPackageName(source.get_package());
        modelTestSuite.setProperties(null);
        modelTestSuite.setTestCases(doFromJunitToModelTestCases(source.getTestcase()));
        modelTestSuite.setTime(source.getTime());
        if (modelTestSuite.getTime() == null) {
            modelTestSuite.setTime(BigDecimal.ZERO);
        }

        return modelTestSuite;
    }

    public static TestResultModel doFromJunitToModelTestResult(Testsuites sources) {
        TestResultModel modelTestResult = new TestResultModel();
        modelTestResult.setTestSuites(doFromJunitToModelTestSuites(sources));
        modelTestResult.setTests(0);
        modelTestResult.setSkipped(0);
        modelTestResult.setFailure(0);
        modelTestResult.setError(0);
        modelTestResult.setTime(BigDecimal.ZERO);

        for (TestSuiteModel testSuite : modelTestResult.getTestSuites()) {
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
