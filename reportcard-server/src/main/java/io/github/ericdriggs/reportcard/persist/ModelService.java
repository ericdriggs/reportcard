package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.model.*;
import org.jooq.DSLContext;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions"})
public class ModelService extends AbstractPersistService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected final ModelMapper mapper;

    @Autowired
    public ModelService(DSLContext dsl, ModelMapper mapper) {
        super(dsl);
        this.mapper = mapper;

    }

    protected void throwNotFound(String... args) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                getNotFoundMessage(args));
    }

    //TODO: refactor to take tuple of variable name and value
    protected static String getNotFoundMessage(String... args) {
        return "Unable to find " + String.join(" <- ", args);
    }

    //TODO: Map<Stage,Map<TestResult,Set<TestSuite>>>
    public TestResultModel getTestResult(Long testResultId) {

        TestResultModel testResult = dsl.
                select(TEST_RESULT.fields())
                .from(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.eq(testResultId))
                .fetchOne().into(TestResultModel.class);

        List<TestSuiteModel> testSuites = dsl.
                select(TEST_SUITE.fields())
                .from(TEST_RESULT
                        .join(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                ).where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                .fetchInto(TestSuiteModel.class);

        testResult.setTestSuites(testSuites);

        for (TestSuiteModel testSuite : testResult.getTestSuites()) {
            List<TestCaseModel> testCases = dsl.
                    select(TEST_CASE.fields())
                    .from(TEST_CASE
                            .join(TEST_SUITE).on(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID))
                    ).where(TEST_SUITE.TEST_SUITE_ID.eq(testSuite.getTestSuiteId()))
                    .fetchInto(TestCaseModel.class);

            testSuite.setTestCases(testCases);
        }
        return testResult;
    }

    //TODO: Map<Stage,Map<TestResult,Set<TestSuite>>>
    public Set<TestResultModel> getTestResults(Long stageId) {

        Set<TestResultModel> testResults = new TreeSet<>(ModelComparators.TEST_RESULT_MODEL_CASE_INSENSITIVE_ORDER);
        testResults.addAll(
                dsl.
                        select(TEST_RESULT.fields())
                        .from(STAGE
                                .join(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                        ).where(STAGE.STAGE_ID.eq(stageId))
                        .fetchInto(TestResultModel.class));

        for (TestResultModel testResult : testResults) {

            List<TestSuiteModel> testSuites = dsl.
                    select(TEST_SUITE.fields())
                    .from(TEST_RESULT
                            .join(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                    ).where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                    .fetchInto(TestSuiteModel.class);

            testResult.setTestSuites(testSuites);

            for (TestSuiteModel testSuite : testResult.getTestSuites()) {
                List<TestCaseModel> testCases = dsl.
                        select(TEST_CASE.fields())
                        .from(TEST_CASE
                                .join(TEST_SUITE).on(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID))
                        ).where(TEST_SUITE.TEST_SUITE_ID.eq(testSuite.getTestSuiteId()))
                        .fetchInto(TestCaseModel.class);

                testSuite.setTestCases(testCases);
            }

        }
        return testResults;
    }

}