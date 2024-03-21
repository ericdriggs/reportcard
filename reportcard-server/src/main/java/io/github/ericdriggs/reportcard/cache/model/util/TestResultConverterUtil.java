package io.github.ericdriggs.reportcard.cache.model.util;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StageTestResult;
import io.github.ericdriggs.reportcard.model.*;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum TestResultConverterUtil {
    ;//static methods only

    final static ModelMapper modelMapper = new ModelMapper();

    public final static Converter<TestCaseFaultPojo, TestCaseFaultModel> testCaseFaultConverter = new AbstractConverter<TestCaseFaultPojo, TestCaseFaultModel>() {
        protected TestCaseFaultModel convert(TestCaseFaultPojo source) {
            TestCaseFaultModel model = new TestCaseFaultModel();
            model.setFaultContextFk(source.getFaultContextFk());
            if (source.getFaultContextFk() != null) {
                model.setFaultContext(FaultContext.fromFaultContextId(source.getFaultContextFk()));
            }
            model.setMessage(source.getMessage());
            model.setTestCaseFaultId(source.getTestCaseFaultId());
            model.setTestCaseFk(source.getTestCaseFk());
            model.setType(source.getType());
            model.setValue(source.getValue());
            return model;
        }
    };

    static {
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                   .setFieldAccessLevel(Configuration.AccessLevel.PUBLIC);
        modelMapper.addConverter(testCaseFaultConverter);
        ;
    }



    public static TestResultModel fromHasTestResultMap(
            Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.HasTestResult,
                    Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuitePojo,
                            Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCasePojo,
                                    List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCaseFaultPojo>>>> hasTestResultMapMap) {
        throw new UnsupportedOperationException("not yet implemented -- blocked on https://github.com/ericdriggs/reportcard/issues/86");
    }

    public static TestResultModel fromStageTestResultMap(
            Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.StageTestResult,
                    Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuitePojo,
                            Map<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCasePojo,
                                    List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCaseFaultPojo>>>> stageTestResultMap) {

        if (stageTestResultMap == null || stageTestResultMap.size() == 0) {
            throw new IllegalArgumentException("stageTestResultMap is missing: " + stageTestResultMap);
        }
        if (stageTestResultMap.size() > 1) {
            throw new IllegalArgumentException("only single stageTestResult allowed. size: " + stageTestResultMap.size());
        }

        TestResultModel testResultModel = null;
        for (Map.Entry<StageTestResult, Map<TestSuitePojo, Map<TestCasePojo, List<TestCaseFaultPojo>>>> stageTestResultEntry : stageTestResultMap.entrySet()) {
            final StageTestResult stageTestResult = stageTestResultEntry.getKey();
            final StagePojo stage = stageTestResult.getStage();
            final TestResultPojo testResultPojo = stageTestResult.getTestResult();

            final Map<TestSuitePojo, Map<TestCasePojo, List<TestCaseFaultPojo>>> testSuiteMap = stageTestResultEntry.getValue();

            List<TestSuiteModel> testSuiteModels = new ArrayList<>();
            for (Map.Entry<TestSuitePojo, Map<TestCasePojo, List<TestCaseFaultPojo>>> testSuiteEntry : testSuiteMap.entrySet()) {
                final TestSuitePojo testSuitePojo = testSuiteEntry.getKey();
                final Map<TestCasePojo, List<TestCaseFaultPojo>> testCaseMap = testSuiteEntry.getValue();


                List<TestCaseModel> testCaseModels = new ArrayList<>();
                for (Map.Entry<TestCasePojo, List<TestCaseFaultPojo>> testCaseEntry : testCaseMap.entrySet()) {
                    final TestCasePojo testCasePojo = testCaseEntry.getKey();
                    final List<TestCaseFaultPojo> testCaseFaultPojos = testCaseEntry.getValue();

                    final List<TestCaseFaultModel> testCaseFaultModels = mapList(testCaseFaultPojos, TestCaseFaultModel.class);
                    final TestCaseModel testCaseModel = fromPojo(testCasePojo, testCaseFaultModels);
                    testCaseModels.add(testCaseModel);
                }
                final TestSuiteModel testSuiteModel = fromPojo(testSuitePojo, testCaseModels);

            }

            return fromPojo(testResultPojo, testSuiteModels);
        }
        throw new IllegalStateException("unreachable code");
    }



    public static TestResultModel fromPojo(TestResultPojo pojo, List<TestSuiteModel> testSuiteModels) {
        TestResultModel model = modelMapper.map(pojo, TestResultModel.class);
        model.setTestSuites(testSuiteModels);
        return model;
    }

    public static TestSuiteModel fromPojo(TestSuitePojo pojo, List<TestCaseModel> testCaseModels) {
        TestSuiteModel model = modelMapper.map(pojo, TestSuiteModel.class);
        model.setTestCases(testCaseModels);
        return model;
    }


    private static TestCaseModel fromPojo(TestCasePojo pojo, List<TestCaseFaultModel> testCaseFaultModels) {
        TestCaseModel model = modelMapper.map(pojo, TestCaseModel.class);
        model.setTestStatus(TestStatus.fromStatusId(pojo.getTestStatusFk()));
        model.setTestCaseFaults(testCaseFaultModels);
        return model;
    }

    public static TestCaseFaultModel fromPojo(TestCaseFaultPojo pojo) {
        TestCaseFaultModel model = modelMapper.map(pojo, TestCaseFaultModel.class);
        model.setFaultContext(FaultContext.fromFaultContextId(pojo.getFaultContextFk()));
        return model;
    }



    public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {

//        for (S s : source) {
//            if ()
//        }
        return source
                .stream()
                .map(element -> modelMapper.map(element, targetClass))
                .collect(Collectors.toList());
    }

}
