package io.github.ericdriggs.reportcard.model.converter.surefire;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.xml.IsEmptyUtil;
import io.github.ericdriggs.reportcard.xml.surefire.*;
import lombok.SneakyThrows;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class SurefireConvertersUtil {

    private final static ObjectMapper mapper = new ObjectMapper();

    public final static Converter<Testcase, TestCaseModel> fromSurefireToModelTestCase = new AbstractConverter<Testcase, TestCaseModel>() {
        protected TestCaseModel convert(io.github.ericdriggs.reportcard.xml.surefire.Testcase source) {
            return doFromSurefireToModelTestCase(source);
        }
    };

    public final static Converter<Testsuite, TestSuiteModel> fromSurefireToModelTestSuite = new AbstractConverter<Testsuite, TestSuiteModel>() {
        protected TestSuiteModel convert(io.github.ericdriggs.reportcard.xml.surefire.Testsuite source) {
            return doFromSurefireToModelTestSuite(source);
        }
    };

    public final static Converter<Collection<Testsuite>, TestResultModel> fromSurefireToModelTestResult = new AbstractConverter<Collection<Testsuite>, TestResultModel>() {
        protected TestResultModel convert(Collection<Testsuite> source) {
            return doFromSurefireToModelTestResult(source);
        }
    };


    public static TestSuiteModel fromTestXmlContent(String testXmlContent ) {
        Testsuite testsuite = SurefireParserUtil.parseTestSuite(testXmlContent);
        return doFromSurefireToModelTestSuite(testsuite);
    }

    public static List<TestCaseModel> doFromSurefireToModelTestCases(List<Testcase> source) {
        List<TestCaseModel> testCases = new ArrayList<>();
        for (Testcase testcase : source) {
            testCases.add(doFromSurefireToModelTestCase(testcase));
        }
        return testCases;
    }

    public static TestCaseModel doFromSurefireToModelTestCase(Testcase source) {
        TestCaseModel modelTestCase = TestCaseModel.builder().build();
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
        modelTestCase.addTestCaseFaults(getTestCaseFaults(source));
        return modelTestCase;
    }

    public static List<TestCaseFaultModel> getTestCaseFaults(Testcase source) {
        List<TestCaseFaultModel> testCaseFaults = new ArrayList<>();
        if (source.getError() != null) {
            testCaseFaults.addAll(getTestCaseFaults(List.of(source.getError().getValue()), FaultContext.ERROR));
        }
        testCaseFaults.addAll(getTestCaseFaults(source.getFailure(), FaultContext.FAILURE));
        testCaseFaults.addAll(getTestCaseFaults(source.getFlakyError(), FaultContext.ERROR));
        testCaseFaults.addAll(getTestCaseFaults(source.getFlakyFailure(), FaultContext.FAILURE));
        testCaseFaults.addAll(getTestCaseFaults(source.getRerunError(), FaultContext.ERROR));
        testCaseFaults.addAll(getTestCaseFaults(source.getRerunFailure(), FaultContext.FAILURE));
        return testCaseFaults;
    }

    public static List<TestCaseFaultModel> getTestCaseFaults(List<? extends HasValueMessageTypeSurefire> faults, FaultContext faultContext) {
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

        TestCaseFaultModel testCaseFault = TestCaseFaultModel.builder().build();
        testCaseFault.setFaultContextFk(faultContext.getFaultContextId())
                     .setMessage(fault.getMessage())
                     .setType(fault.getType())
                     .setValue(fault.getValue());
        return testCaseFault;
    }

    public static List<TestSuiteModel> doFromSurefireToModelTestSuites(Collection<Testsuite> source) {
        List<TestSuiteModel> testSuites = new ArrayList<>();
        for (Testsuite testsuite : source) {
            testSuites.add(doFromSurefireToModelTestSuite(testsuite));
        }
        return testSuites;
    }

    public static TestSuiteModel doFromSurefireToModelTestSuite(Testsuite source) {
        TestSuiteModel modelTestSuite = TestSuiteModel.builder().build();
        modelTestSuite.setName(source.getName());
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
        if (modelTestSuite.getSkipped() > 0 || modelTestSuite.getTests() == 0) {
            modelTestSuite.setHasSkip(true);
        }
        modelTestSuite.setSkipped(source.getSkipped());
        if (modelTestSuite.getSkipped() == null) {
            modelTestSuite.setSkipped(0);
        }
        modelTestSuite.setGroup(source.getGroup());
        modelTestSuite.setPackageName(null);
        modelTestSuite.setProperties(convertPropertiesList(source.getProperties()));
        modelTestSuite.setTestCases(doFromSurefireToModelTestCases(source.getTestcase()));
        modelTestSuite.setTime(source.getTime());
        if (modelTestSuite.getTime() == null) {
            modelTestSuite.setTime(BigDecimal.ZERO);
        }

        modelTestSuite.setIsSuccess(modelTestSuite.getError() == 0 && modelTestSuite.getFailure() == 0 && modelTestSuite.getTests() > 0);

        return modelTestSuite;
    }

    @SneakyThrows(JsonProcessingException.class)
    public static String convertPropertiesList(List<io.github.ericdriggs.reportcard.xml.surefire.Properties> surefirePropertiesList) {
        if (surefirePropertiesList == null) {
            return null;
        }

        List<Properties> propertiesList = new ArrayList<>();
        for (io.github.ericdriggs.reportcard.xml.surefire.Properties surefireProperties : surefirePropertiesList) {

            Properties properties = new Properties();
            if (surefireProperties.getProperty() == null) {
                continue;
            }
            List<Property> surefirePropertyList = surefireProperties.getProperty();
            for (Property property : surefirePropertyList) {
                properties.setProperty(property.getName(), property.getValue());
            }
            propertiesList.add(properties);
        }
        return mapper.writeValueAsString(propertiesList);
    }

    public static TestResultModel doFromSurefireToModelTestResult(Collection<Testsuite> sources) {
        TestResultModel modelTestResult = new TestResultModel();
        modelTestResult.setTestSuites(doFromSurefireToModelTestSuites(sources));
        modelTestResult.setTests(0);
        modelTestResult.setSkipped(0);
        modelTestResult.setFailure(0);
        modelTestResult.setError(0);
        modelTestResult.setTime(BigDecimal.ZERO);

        boolean hasSkip = false;
        for (TestSuiteModel testSuite : modelTestResult.getTestSuites()) {
            modelTestResult.setTime(modelTestResult.getTime().add(testSuite.getTime()));
            modelTestResult.setTests(modelTestResult.getTests() + testSuite.getTests());
            modelTestResult.setSkipped(modelTestResult.getSkipped() + testSuite.getSkipped());
            modelTestResult.setFailure(modelTestResult.getFailure() + testSuite.getFailure());
            modelTestResult.setError(modelTestResult.getError() + testSuite.getError());
        }

        if (modelTestResult.getSkipped() > 0) {
            modelTestResult.setHasSkip(true);
        } else {
            modelTestResult.setHasSkip(false);
        }

        if (modelTestResult.getFailure() > 0 || modelTestResult.getError() > 0) {
            modelTestResult.setIsSuccess(false);
        } else {
            modelTestResult.setIsSuccess(true);
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
