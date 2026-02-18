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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TagQueryUIController using MockMvc.
 */
@WebMvcTest(TagQueryUIController.class)
public class TagQueryUIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagQueryService tagQueryService;

    @Test
    void testSearchFormDisplayed() throws Exception {
        mockMvc.perform(get("/company/testco/tags/tests"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(content().string(containsString("<legend>Tag Search</legend>")))
            .andExpect(content().string(containsString("<input type=\"text\" id=\"tags\" name=\"tags\"")))
            .andExpect(content().string(containsString("<button type=\"submit\">Search</button>")))
            .andExpect(content().string(containsString("smoke AND env=prod")));
    }

    @Test
    void testEmptyTagsShowsForm() throws Exception {
        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", ""))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(content().string(containsString("<legend>Tag Search</legend>")));
    }

    @Test
    void testValidExpressionReturnsResults() throws Exception {
        // Build response with proper hierarchy for org scope
        TagQueryResponse response = TagQueryResponse.builder()
            .query(QueryInfo.builder()
                .scope("/company/testco")
                .tags("smoke")
                .build())
            .orgs(List.of(
                OrgResult.builder()
                    .orgId(1)
                    .orgName("testorg")
                    .repos(List.of(
                        RepoResult.builder()
                            .repoId(10)
                            .repoName("testrepo")
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
                                                    .sha("abc123def")
                                                    .runDate(Instant.parse("2024-01-15T10:30:00Z"))
                                                    .stages(List.of(
                                                        StageResult.builder()
                                                            .stageId(8000L)
                                                            .stageName("test")
                                                            .tests(List.of(
                                                                TestInfo.builder()
                                                                    .testName("testLogin")
                                                                    .className("SmokeTest")
                                                                    .status("PASSED")
                                                                    .build(),
                                                                TestInfo.builder()
                                                                    .testName("testLogout")
                                                                    .className("SmokeTest")
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
            eq("smoke"), eq("testco"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(response);

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(content().string(containsString("<legend>Query Info</legend>")))
            .andExpect(content().string(containsString("smoke")))
            .andExpect(content().string(containsString("class=\"org-fieldset\"")))
            .andExpect(content().string(containsString("class=\"repo-fieldset\"")))
            .andExpect(content().string(containsString("class=\"branch-fieldset\"")))
            .andExpect(content().string(containsString("class=\"job-fieldset\"")))
            .andExpect(content().string(containsString("class=\"run-fieldset\"")))
            .andExpect(content().string(containsString("class=\"stage-fieldset\"")))
            .andExpect(content().string(containsString("main")))
            .andExpect(content().string(containsString("abc123def")))
            .andExpect(content().string(containsString("SmokeTest.testLogin")))
            .andExpect(content().string(containsString("SmokeTest.testLogout")));

        verify(tagQueryService).findByTagExpressionByPath("smoke", "testco", null, null, null, null);
    }

    @Test
    void testInvalidExpressionReturnsError() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            eq("AND AND"), eq("testco"), isNull(), isNull(), isNull(), isNull()
        )).thenThrow(new ParseException("Unexpected token 'AND'", 4));

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "AND AND"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(content().string(containsString("class=\"error\"")))
            .andExpect(content().string(containsString("Unexpected token")))
            .andExpect(content().string(containsString("value=\"AND AND\"")))
            .andExpect(content().string(containsString("<legend>Tag Search</legend>")));
    }

    @Test
    void testAllHierarchyLevels_Company() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), eq("testco"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(emptyResponse("/company/testco", "smoke"));

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("No results found")));
    }

    @Test
    void testAllHierarchyLevels_Org() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), eq("testco"), eq("testorg"), isNull(), isNull(), isNull()
        )).thenReturn(emptyResponse("/company/testco/org/testorg", "smoke"));

        mockMvc.perform(get("/company/testco/org/testorg/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/company/testco/org/testorg")));
    }

    @Test
    void testAllHierarchyLevels_Repo() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), eq("testco"), eq("testorg"), eq("testrepo"), isNull(), isNull()
        )).thenReturn(emptyResponse("/company/testco/org/testorg/repo/testrepo", "smoke"));

        mockMvc.perform(get("/company/testco/org/testorg/repo/testrepo/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/company/testco/org/testorg/repo/testrepo")));
    }

    @Test
    void testAllHierarchyLevels_Branch() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), eq("testco"), eq("testorg"), eq("testrepo"), eq("main"), isNull()
        )).thenReturn(emptyResponse("/company/testco/org/testorg/repo/testrepo/branch/main", "smoke"));

        mockMvc.perform(get("/company/testco/org/testorg/repo/testrepo/branch/main/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/company/testco/org/testorg/repo/testrepo/branch/main")));
    }

    @Test
    void testAllHierarchyLevels_Sha() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), eq("testco"), eq("testorg"), eq("testrepo"), eq("main"), eq("abc123")
        )).thenReturn(emptyResponse("/company/testco/org/testorg/repo/testrepo/branch/main/sha/abc123", "smoke"));

        mockMvc.perform(get("/company/testco/org/testorg/repo/testrepo/branch/main/sha/abc123/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/company/testco/org/testorg/repo/testrepo/branch/main")));
    }

    @Test
    void testNoResults_ShowsMessage() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            eq("nonexistent"), eq("testco"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(emptyResponse("/company/testco", "nonexistent"));

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "nonexistent"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("No results found for this query")));
    }

    @Test
    void testMultipleOrgsAndRepos() throws Exception {
        // Build response with multiple orgs and repos
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
                                                    .sha("sha1")
                                                    .runDate(Instant.parse("2024-01-15T10:30:00Z"))
                                                    .stages(List.of(
                                                        StageResult.builder()
                                                            .stageId(8000L)
                                                            .stageName("test")
                                                            .tests(List.of(
                                                                TestInfo.builder()
                                                                    .testName("Test1")
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
                    .build(),
                OrgResult.builder()
                    .orgId(2)
                    .orgName("org2")
                    .repos(List.of(
                        RepoResult.builder()
                            .repoId(20)
                            .repoName("repo2")
                            .branches(List.of(
                                BranchResult.builder()
                                    .branchId(200)
                                    .branchName("develop")
                                    .jobs(List.of(
                                        JobResult.builder()
                                            .jobId(2000L)
                                            .jobInfo(new TreeMap<>())
                                            .runs(List.of(
                                                RunResult.builder()
                                                    .runId(6000L)
                                                    .sha("sha2")
                                                    .runDate(Instant.parse("2024-01-16T11:30:00Z"))
                                                    .stages(List.of(
                                                        StageResult.builder()
                                                            .stageId(9000L)
                                                            .stageName("test")
                                                            .tests(List.of(
                                                                TestInfo.builder()
                                                                    .testName("Test3")
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
            eq("smoke"), eq("testco"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(response);

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("org1")))
            .andExpect(content().string(containsString("org2")))
            .andExpect(content().string(containsString("repo1")))
            .andExpect(content().string(containsString("repo2")))
            .andExpect(content().string(containsString("main")))
            .andExpect(content().string(containsString("develop")))
            .andExpect(content().string(containsString("sha1")))
            .andExpect(content().string(containsString("sha2")))
            .andExpect(content().string(containsString("Test1")))
            .andExpect(content().string(containsString("Test3")));
    }

    @Test
    void testXssProtection_EscapesUserInput() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            eq("<script>alert('xss')</script>"), eq("testco"), isNull(), isNull(), isNull(), isNull()
        )).thenThrow(new ParseException("Invalid", 0));

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "<script>alert('xss')</script>"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("&lt;script&gt;")));
    }

    private TagQueryResponse emptyResponse(String scope, String tags) {
        return TagQueryResponse.builder()
            .query(QueryInfo.builder().scope(scope).tags(tags).build())
            .build();
    }
}
