package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StageDetailsTest {

    @Test
    void jobInfoTest() {
        TreeMap<String, String> jobInfoMap = new TreeMap<>();
        jobInfoMap.put("host", "host1");
        jobInfoMap.put("application", "application1");
        jobInfoMap.put("pipeline", "pipeline1");

        StageDetails stageDetails = StageDetails
                .builder()
                .company(TestData.company)
                .org(TestData.org)
                .repo(TestData.repo)
                .branch(TestData.branch)
                .sha(TestData.sha)
                .stage(TestData.stage)
                .jobInfo(jobInfoMap)
                .build();
        assertEquals("{\"application\":\"application1\",\"host\":\"host1\",\"pipeline\":\"pipeline1\"}", stageDetails.getJobInfoJson());
    }

    @Test
    void jobInfoStripsHtmlChars() {
        TreeMap<String, String> jobInfoMap = new TreeMap<>();
        jobInfoMap.put("application", "<script>alert(1)</script>");
        jobInfoMap.put("pipeline", "foo&bar");
        jobInfoMap.put("<img>", "malicious-key");

        StageDetails stageDetails = StageDetails.builder()
                .company(TestData.company).org(TestData.org).repo(TestData.repo)
                .branch(TestData.branch).sha(TestData.sha).stage(TestData.stage)
                .jobInfo(jobInfoMap).build();

        assertEquals("scriptalert(1)/script", stageDetails.getJobInfo().get("application"));
        assertEquals("foobar", stageDetails.getJobInfo().get("pipeline"));
        assertEquals("malicious-key", stageDetails.getJobInfo().get("img"));
    }

    @Test
    void entityNamesStripHtmlChars() {
        TreeMap<String, String> jobInfoMap = new TreeMap<>();
        jobInfoMap.put("pipeline", "safe");

        StageDetails stageDetails = StageDetails.builder()
                .company("my<company").org("my>org").repo("my\"repo")
                .branch("my'branch").sha(TestData.sha).stage("my&stage")
                .jobInfo(jobInfoMap).build();

        assertEquals("mycompany", stageDetails.getCompany());
        assertEquals("myorg", stageDetails.getOrg());
        assertEquals("myrepo", stageDetails.getRepo());
        assertEquals("mybranch", stageDetails.getBranch());
        assertEquals("mystage", stageDetails.getStage());
    }

    @Test
    void entityNamesPreserveSlashesHyphensUnderscores() {
        TreeMap<String, String> jobInfoMap = new TreeMap<>();
        jobInfoMap.put("pipeline", "my-pipeline");

        StageDetails stageDetails = StageDetails.builder()
                .company("my-company").org("my_org").repo("my.repo")
                .branch("feature/my-branch").sha(TestData.sha).stage("unit-tests_v2")
                .jobInfo(jobInfoMap).build();

        assertEquals("my-company", stageDetails.getCompany());
        assertEquals("my_org", stageDetails.getOrg());
        assertEquals("my.repo", stageDetails.getRepo());
        assertEquals("feature_my-branch", stageDetails.getBranch());
        assertEquals("unit-tests_v2", stageDetails.getStage());
    }

    @Test
    void shaStripsHtmlChars() {
        TreeMap<String, String> jobInfoMap = new TreeMap<>();
        jobInfoMap.put("pipeline", "safe");

        StageDetails stageDetails = StageDetails.builder()
                .company(TestData.company).org(TestData.org).repo(TestData.repo)
                .branch(TestData.branch).sha("abc<script>123").stage(TestData.stage)
                .jobInfo(jobInfoMap).build();

        assertEquals("abcscript123", stageDetails.getSha());
    }

    @Test
    void jobInfoPreservesHyphensDotsUnderscores() {
        TreeMap<String, String> jobInfoMap = new TreeMap<>();
        jobInfoMap.put("application", "foo-app");
        jobInfoMap.put("host", "build.corp.jenkins.com");
        jobInfoMap.put("pipeline", "dev-cp3");
        jobInfoMap.put("env", "prod_us-east-1");

        StageDetails stageDetails = StageDetails.builder()
                .company(TestData.company).org(TestData.org).repo(TestData.repo)
                .branch(TestData.branch).sha(TestData.sha).stage(TestData.stage)
                .jobInfo(jobInfoMap).build();

        assertEquals("foo-app", stageDetails.getJobInfo().get("application"));
        assertEquals("build.corp.jenkins.com", stageDetails.getJobInfo().get("host"));
        assertEquals("dev-cp3", stageDetails.getJobInfo().get("pipeline"));
        assertEquals("prod_us-east-1", stageDetails.getJobInfo().get("env"));
    }
}
