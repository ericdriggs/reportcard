package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.model.TagQueryResponse;
import io.github.ericdriggs.reportcard.model.TagQueryResponse.*;
import io.github.ericdriggs.reportcard.persist.tags.ParseException;
import io.github.ericdriggs.reportcard.persist.tags.TagQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TagQueryController using MockMvc.
 */
@WebMvcTest(TagQueryController.class)
public class TagQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagQueryService tagQueryService;

    @Test
    void searchByTags_companyLevel_returnsOk() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(emptyResponse("/company/testco", "smoke"));

        mockMvc.perform(get("/json/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("/company/testco"))
            .andExpect(jsonPath("$.query.tags").value("smoke"));
    }

    @Test
    void searchByTags_orgLevel_returnsOk() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), anyString(), isNull(), isNull(), isNull()
        )).thenReturn(emptyResponse("/company/testco/org/testorg", "smoke"));

        mockMvc.perform(get("/json/company/testco/org/testorg/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("/company/testco/org/testorg"))
            .andExpect(jsonPath("$.query.tags").value("smoke"));
    }

    @Test
    void searchByTags_repoLevel_returnsOk() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), anyString(), anyString(), isNull(), isNull()
        )).thenReturn(emptyResponse("/company/testco/org/testorg/repo/testrepo", "smoke AND env=prod"));

        mockMvc.perform(get("/json/company/testco/org/testorg/repo/testrepo/tags/tests")
                .param("tags", "smoke AND env=prod"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("/company/testco/org/testorg/repo/testrepo"))
            .andExpect(jsonPath("$.query.tags").value("smoke AND env=prod"));
    }

    @Test
    void searchByTags_branchLevel_returnsOk() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), anyString(), anyString(), anyString(), isNull()
        )).thenReturn(emptyResponse("/company/testco/org/testorg/repo/testrepo/branch/main", "regression"));

        mockMvc.perform(get("/json/company/testco/org/testorg/repo/testrepo/branch/main/tags/tests")
                .param("tags", "regression"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("/company/testco/org/testorg/repo/testrepo/branch/main"))
            .andExpect(jsonPath("$.query.tags").value("regression"));
    }

    @Test
    void searchByTags_shaLevel_returnsOk() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(emptyResponse("/company/co/org/o/repo/r/branch/b/sha/s", "smoke"));

        mockMvc.perform(get("/json/company/co/org/o/repo/r/branch/b/sha/s/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("/company/co/org/o/repo/r/branch/b/sha/s"));
    }

    @Test
    void searchByTags_missingTagsParam_returns400() throws Exception {
        mockMvc.perform(get("/json/company/testco/tags/tests"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchByTags_invalidExpression_returns400() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), isNull(), isNull(), isNull(), isNull()
        )).thenThrow(new ParseException("Unexpected token", 0));

        mockMvc.perform(get("/json/company/testco/tags/tests")
                .param("tags", "AND smoke"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid tag expression"));
    }

    @Test
    void searchByTags_complexExpression_acceptsUrlEncodedSyntax() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(emptyResponse("/company/testco", "(smoke OR regression) AND env=prod"));

        mockMvc.perform(get("/json/company/testco/tags/tests")
                .param("tags", "(smoke OR regression) AND env=prod"))
            .andExpect(status().isOk());
    }

    @Test
    void searchByTags_emptyExpression_returns400() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), isNull(), isNull(), isNull(), isNull()
        )).thenThrow(new ParseException("Empty expression"));

        mockMvc.perform(get("/json/company/testco/tags/tests")
                .param("tags", ""))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid tag expression"));
    }

    @Test
    void searchByTags_serviceReturnsResults_includesInResponse() throws Exception {
        // Build response with proper hierarchy
        TagQueryResponse response = TagQueryResponse.builder()
            .query(QueryInfo.builder()
                .scope("/company/testco")
                .tags("smoke")
                .build())
            .orgs(List.of(
                OrgResult.builder()
                    .orgId(1)
                    .orgName("org1")
                    .repos(List.of(
                        RepoResult.builder()
                            .repoId(10)
                            .repoName("repo1")
                            .branches(List.of(
                                BranchResult.builder()
                                    .branchId(100)
                                    .branchName("main")
                                    .jobs(List.of(
                                        JobResult.builder()
                                            .jobId(1000L)
                                            .jobInfo(new TreeMap<>())
                                            .runs(List.of(
                                                RunResult.builder()
                                                    .runId(5000L)
                                                    .sha("abc123")
                                                    .runDate(Instant.parse("2024-01-15T10:30:00Z"))
                                                    .stages(List.of(
                                                        StageResult.builder()
                                                            .stageId(8000L)
                                                            .stageName("test")
                                                            .tests(List.of(
                                                                TestInfo.builder()
                                                                    .testName("test1")
                                                                    .className("TestClass")
                                                                    .status("PASSED")
                                                                    .build(),
                                                                TestInfo.builder()
                                                                    .testName("test2")
                                                                    .className("TestClass")
                                                                    .status("PASSED")
                                                                    .build()
                                                            ))
                                                            .build()
                                                    ))
                                                    .build()
                                            ))
                                            .build()
                                    ))
                                    .build()
                            ))
                            .build()
                    ))
                    .build()
            ))
            .build();

        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(response);

        mockMvc.perform(get("/json/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orgs[0].orgName").value("org1"))
            .andExpect(jsonPath("$.orgs[0].repos[0].repoName").value("repo1"))
            .andExpect(jsonPath("$.orgs[0].repos[0].branches[0].branchName").value("main"))
            .andExpect(jsonPath("$.orgs[0].repos[0].branches[0].jobs[0].runs[0].sha").value("abc123"))
            .andExpect(jsonPath("$.orgs[0].repos[0].branches[0].jobs[0].runs[0].stages[0].tests[0].testName").value("test1"))
            .andExpect(jsonPath("$.orgs[0].repos[0].branches[0].jobs[0].runs[0].stages[0].tests[1].testName").value("test2"));
    }

    private TagQueryResponse emptyResponse(String scope, String tags) {
        return TagQueryResponse.builder()
            .query(QueryInfo.builder().scope(scope).tags(tags).build())
            .build();
    }
}
