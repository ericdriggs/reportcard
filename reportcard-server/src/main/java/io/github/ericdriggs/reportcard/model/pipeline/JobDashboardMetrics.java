package io.github.ericdriggs.reportcard.model.pipeline;

import io.github.ericdriggs.reportcard.model.graph.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static io.github.ericdriggs.reportcard.util.list.ListAssertUtil.emptyIfNull;

@Builder
@Jacksonized
@Value
public class JobDashboardMetrics {
    String company;
    String org;
    String repo;
    String branch;
    String jobInfo;
    
    // Core metrics from requirements
    Integer daysSincePassingRun; // null if N/A
    BigDecimal jobPassPercent;
    // jobTimeAverage out of scope for V1
    BigDecimal testPassPercent;
    
    // Supporting data
    Instant lastPassingRun;
    Integer totalRuns;
    Integer passingRuns;
    
    public static List<JobDashboardMetrics> fromCompanyGraphs(List<CompanyGraph> companyGraphs, JobDashboardRequest request) {
        List<JobDashboardMetrics> results = new ArrayList<>();
        
        for (CompanyGraph companyGraph : emptyIfNull(companyGraphs)) {
            for (OrgGraph orgGraph : emptyIfNull(companyGraph.orgs())) {
                for (RepoGraph repoGraph : emptyIfNull(orgGraph.repos())) {
                    for (BranchGraph branchGraph : emptyIfNull(repoGraph.branches())) {
                        for (JobGraph jobGraph : emptyIfNull(branchGraph.jobs())) {
                            
                            // Calculate metrics from runs and test results
                            List<RunGraph> runs = emptyIfNull(jobGraph.runs());
                            
                            // Days since passing run
                            Instant lastPassingRun = null;
                            for (RunGraph run : runs) {
                                if (run.isSuccess() && (lastPassingRun == null || run.runDate().isAfter(lastPassingRun))) {
                                    lastPassingRun = run.runDate();
                                }
                            }
                            Integer daysSincePassing = lastPassingRun != null ? 
                                (int) ChronoUnit.DAYS.between(lastPassingRun, Instant.now()) : null;
                            
                            // Job pass percentage
                            int totalRuns = runs.size();
                            int passingRuns = (int) runs.stream().mapToInt(r -> r.isSuccess() ? 1 : 0).sum();
                            BigDecimal jobPassPercent = totalRuns > 0 ? 
                                BigDecimal.valueOf(passingRuns * 100.0 / totalRuns) : BigDecimal.ZERO;
                            
                            // Test pass percentage
                            int totalTests = 0;
                            int passingTests = 0;
                            for (RunGraph run : runs) {
                                for (StageGraph stage : emptyIfNull(run.stages())) {
                                    for (TestResultGraph testResult : emptyIfNull(stage.testResults())) {
                                        totalTests += testResult.tests();
                                        if (testResult.isSuccess()) {
                                            passingTests += testResult.tests();
                                        }
                                    }
                                }
                            }
                            BigDecimal testPassPercent = totalTests > 0 ? 
                                BigDecimal.valueOf(passingTests * 100.0 / totalTests) : BigDecimal.ZERO;
                            
                            results.add(JobDashboardMetrics.builder()
                                .company(companyGraph.companyName())
                                .org(orgGraph.orgName())
                                .repo(repoGraph.repoName())
                                .branch(branchGraph.branchName())
                                .jobInfo(jobGraph.jobInfoStr())
                                .daysSincePassingRun(daysSincePassing)
                                .jobPassPercent(jobPassPercent)
                                .testPassPercent(testPassPercent)
                                .lastPassingRun(lastPassingRun)
                                .totalRuns(totalRuns)
                                .passingRuns(passingRuns)
                                .build());
                        }
                    }
                }
            }
        }
        
        return results;
    }
}