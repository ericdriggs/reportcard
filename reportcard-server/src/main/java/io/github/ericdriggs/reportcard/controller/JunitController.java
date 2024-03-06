package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/api/junit")
@SuppressWarnings("unused")
public class JunitController {

    @Autowired
    public JunitController(TestResultPersistService testResultPersistService) {
        this.testResultPersistService = testResultPersistService;
    }

    private final TestResultPersistService testResultPersistService;

    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<Map<StagePath, TestResult>> postJunitXml(
            @RequestParam("company") String company,
            @RequestParam("org") String org,
            @RequestParam("repo") String repo,
            @RequestParam("branch") String branch,
            @RequestParam("sha") String sha,
            @RequestParam("stage") String stage,
            @RequestParam(value = "jobInfo", required = false) String jobInfo,
            @RequestParam(value = "runReference", required = false) String runReference,
            @RequestParam(value = "externalLinks", required = false) String externalLinks,
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))

            //@Schema(type = "array", format = "binary", implementation = String.class)
            MultipartFile file
    ) {
        StageDetails stageDetails = StageDetails.builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .sha(sha)
                .stage(stage)
                .jobInfo(StringMapUtil.stringToMap(jobInfo))
                .runReference(runReference)
                .externalLinks(StringMapUtil.stringToMap(externalLinks))
                .build();

        stageDetails.validateAndSetDefaults();

        return postJunitXmlStageDetails(stageDetails, file);
    }

    @PostMapping(path = "stage-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<Map<StagePath, TestResult>> postJunitXmlStageDetails(
            @Parameter(schema = @Schema(type = "string", format = "binary", implementation = StageDetails.class))
            @RequestPart("stageDetails") StageDetails stageDetails,
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            //@Schema(type = "array", format = "binary", implementation = String.class)
            MultipartFile file

    ) {
        MultipartFile[] files = new MultipartFile[1];
        files[0] = file;
        Map<StagePath, TestResult> stagePathTestResultMap = testResultPersistService.doPostXml(stageDetails, files);
        log.info("post success for postJunitXml -- stageDetails: {}", stageDetails);
        return new ResponseEntity<>(stagePathTestResultMap, HttpStatus.OK);
    }

    @PostMapping("run/{runId}/stage/{stage}")
    public ResponseEntity<Map<StagePath, TestResult>> postJunitXmlNewStageForRun(
            @RequestParam("files") MultipartFile[] files,
            @PathVariable("runId") Long runId,
            @PathVariable("stage") String stage
    ) {
        Map<StagePath, TestResult> stagePathTestResultMap = testResultPersistService.doPostXml(runId, stage, files);
        log.info("post success for postJunitXmlAddStage -- runId: {}. stage: {}: ", runId, stage);
        return new ResponseEntity<>(stagePathTestResultMap, HttpStatus.OK);
    }

}