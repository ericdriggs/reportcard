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
}
