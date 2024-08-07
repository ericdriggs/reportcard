package io.github.ericdriggs.reportcard.gen.db;

import io.github.ericdriggs.reportcard.model.StageDetails;

import java.util.TreeMap;
import java.util.UUID;

public enum TestData {
    ;
    public final static String company = "company1";
    public final static String org = "org1";
    public final static String repo = "repo1";
    public final static String branch = "master";
    public final static String sha = "bdd15b6fae26738ca58f0b300fc43f5872b429bf";
    public static final Long testResultId = 1L;
    public static final Long testSuiteId = 1L;
    public static final UUID runReference = UUID.fromString("aaaaaaaa-2222-bbbb-cccc-dddddddddddd");
    public final static Long jobId = 1l;
    public final static TreeMap<String, String> jobInfo = new TreeMap<>();

    static {
        jobInfo.put("host", "foocorp.jenkins.com");
        jobInfo.put("application", "fooapp");
        jobInfo.put("pipeline", "foopipeline");
    }

    public final static String stage = "api";

    public final static Integer expectedTestCaseCount = 2;
    public final static Integer testResultTestPojoCount = 70;
    public final static Integer testSuiteTestCount = 8;

    public final static StageDetails stageDetails = StageDetails.builder()
            .company(company)
            .org(org)
            .repo(repo)
            .branch(branch)
            .sha(sha)
            .jobInfo(jobInfo)
            .runReference(runReference)
            .stage(stage)
            .build();
}