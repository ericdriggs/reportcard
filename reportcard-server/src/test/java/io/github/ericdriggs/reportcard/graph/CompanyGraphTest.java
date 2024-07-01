package io.github.ericdriggs.reportcard.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import io.github.ericdriggs.reportcard.model.FaultContextType;
import io.github.ericdriggs.reportcard.model.TestStatus;
import io.github.ericdriggs.reportcard.model.graph.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class CompanyGraphTest {

    final ObjectMapper mapper = SharedObjectMappers.simpleObjectMapper;

    @Test
    void serializeDeserializeTest() throws JsonProcessingException {
        List<CompanyGraph> companies = List.of(getCompanyGraph());
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(companies);
        System.out.println("json: " + json);
        List<CompanyGraph> companiesParsed = List.of(mapper.readValue(json, CompanyGraph[].class));
        assertEquals(companies, companiesParsed);
    }

    public static CompanyGraph getCompanyGraph() {
        final Instant now = Instant.now();
        TestCaseFaultGraph testCaseFault = TestCaseFaultGraphBuilder
                .builder()
                .faultContextFk(FaultContextType.FAILURE.getId())
                .message("message1")
                .testCaseFaultId(1L)
                .testCaseFk(1L)
                .type("type1")
                .value("value1")
                .build();

        TestCaseGraph testCase = TestCaseGraphBuilder
                .builder()
                .assertions("assertions1")
                .className("class1")
                .name("testCase1")
                .systemErr("systemErr1")
                .systemOut("systemOut1")
                .testCaseId(1L)
                .testCaseFaults(List.of(testCaseFault))
                .testStatusFk(TestStatus.FAILURE.getStatusId())
                .testSuiteFk(1L)
                .build();

        TestSuiteGraph testSuite = TestSuiteGraphBuilder
                .builder()
                .error(0)
                .failure(1)
                .group("group1")
                .hasSkip(false)
                .isSuccess(false)
                .name("testSuite1")
                .packageName("package1")
                .properties("properties1")
                .skipped(0)
                .testCases(List.of(testCase))
                .tests(1)
                .time(BigDecimal.ONE)
                .build();

        TestResultGraph testResult = TestResultGraphBuilder
                .builder()
                .error(0)
                .externalLinks("externalLinks1")
                .failure(1)
                .hasSkip(false)
                .isSuccess(false)
                .skipped(0)
                .stageFk(1L)
                .tests(1)
                .testResultId(1L)
                .testSuites(List.of(testSuite))
                .testResultCreated(now)
                .build();

        StageGraph stage = StageGraphBuilder
                .builder()
                .stageId(1L)
                .stageName("stage1")
                .build();

        RunGraph run = RunGraphBuilder
                .builder()
                .isSuccess(false)
                .jobFk(1L)
                .jobRunCount(1)
                .runDate(now)
                .runId(1L)
                .runReference("runReference1")
                .sha("sha1")
                .stages(List.of(stage))
                .build();

        JobGraph job = JobGraphBuilder
                .builder()
                .branchFk(1)
                .lastRun(now)
                .jobId(1L)
                .jobInfo(new TreeMap<>(Collections.singletonMap("foo", "bar")))
                .jobInfoStr("{\"foo\":\"bar\"}")
                .runs(List.of(run))
                .build();

        BranchGraph branch = BranchGraphBuilder
                .builder()
                .branchId(1)
                .branchName("branch1")
                .repoFk(1)
                .lastRun(now)
                .jobs(List.of(job))
                .build();

        RepoGraph repo = RepoGraphBuilder
                .builder()
                .branches(List.of(branch))
                .orgFk(1)
                .repoId(1)
                .repoName("repo1")
                .build();

        OrgGraph org = OrgGraphBuilder
                .builder()
                .companyFk(1)
                .orgId(1)
                .orgName("org1")
                .repos(List.of(repo))
                .build();

        CompanyGraph company = CompanyGraphBuilder
                .builder()
                .companyId(1)
                .companyName("company1")
                .orgs(List.of(org))
                .build();

        return company;
    }

}
