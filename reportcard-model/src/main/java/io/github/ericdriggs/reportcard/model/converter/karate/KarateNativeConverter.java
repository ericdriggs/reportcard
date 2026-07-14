package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public enum KarateNativeConverter {
    ; //static methods only

    public static TestCaseModel doFromNativeToModelTestCase(KarateNativeScenario source) {
        TestCaseModel result = new TestCaseModel();
        result.setName(source.getName());
        if (source.getStartTime() != null && source.getEndTime() != null) {
            result.setStartTime(Instant.ofEpochMilli(source.getStartTime()));
            result.setEndTime(Instant.ofEpochMilli(source.getEndTime()));
        }
        return result;
    }

    public static List<TestCaseModel> doFromNativeToModelTestCases(List<KarateNativeScenario> scenarios) {
        List<TestCaseModel> result = new ArrayList<>();
        if (scenarios == null) {
            return result;
        }
        for (KarateNativeScenario scenario : scenarios) {
            result.add(doFromNativeToModelTestCase(scenario));
        }
        return result;
    }

    public static TestSuiteModel doFromNativeToModelTestSuite(KarateNativeFeature source) {
        TestSuiteModel result = new TestSuiteModel();
        result.setPackageName(source.getPackageQualifiedName());
        result.setName(source.getRelativePath());
        result.setTestCases(doFromNativeToModelTestCases(source.getScenarios()));
        return result;
    }

    public static List<TestSuiteModel> fromNativeFeatures(List<KarateNativeFeature> features) {
        List<TestSuiteModel> result = new ArrayList<>();
        if (features == null) {
            return result;
        }
        for (KarateNativeFeature feature : features) {
            result.add(doFromNativeToModelTestSuite(feature));
        }
        return result;
    }

    static final AbstractConverter<KarateNativeScenario, TestCaseModel> fromNativeToModelTestCase = new AbstractConverter<>() {
        @Override
        protected TestCaseModel convert(KarateNativeScenario source) {
            return doFromNativeToModelTestCase(source);
        }
    };

    static final AbstractConverter<KarateNativeFeature, TestSuiteModel> fromNativeToModelTestSuite = new AbstractConverter<>() {
        @Override
        protected TestSuiteModel convert(KarateNativeFeature source) {
            return doFromNativeToModelTestSuite(source);
        }
    };

    static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addConverter(fromNativeToModelTestCase);
        modelMapper.addConverter(fromNativeToModelTestSuite);
    }
}
