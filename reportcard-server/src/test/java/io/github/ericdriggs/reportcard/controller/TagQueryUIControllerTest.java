package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.model.TagQueryResponse;
import io.github.ericdriggs.reportcard.persist.tags.ParseException;
import io.github.ericdriggs.reportcard.persist.tags.TagQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TagQueryUIController using MockMvc.
 *
 * <p>Tests verify HTML rendering including:
 * - Search form display
 * - Results rendering with hierarchy
 * - Error handling with form
 * - All hierarchy level endpoints
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
        // Setup mock data
        TagQueryResponse.JobResult jobResult = TagQueryResponse.JobResult.builder()
            .runDate(Instant.parse("2024-01-15T10:30:00Z"))
            .tests(List.of("SmokeTest.testLogin", "SmokeTest.testLogout"))
            .build();

        Map<String, TagQueryResponse.JobResult> jobMap = new LinkedHashMap<>();
        jobMap.put("jobInfo1", jobResult);

        Map<String, Map<String, TagQueryResponse.JobResult>> shaMap = new LinkedHashMap<>();
        shaMap.put("abc123def", jobMap);

        Map<String, Map<String, Map<String, TagQueryResponse.JobResult>>> results = new LinkedHashMap<>();
        results.put("main", shaMap);

        when(tagQueryService.findByTagExpressionByPath(
            eq("smoke"), eq("testco"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(results);

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(content().string(containsString("<legend>Query Info</legend>")))
            .andExpect(content().string(containsString("smoke")))
            .andExpect(content().string(containsString("class=\"branch-fieldset\"")))
            .andExpect(content().string(containsString("class=\"sha-fieldset\"")))
            .andExpect(content().string(containsString("main")))
            .andExpect(content().string(containsString("abc123def")))
            .andExpect(content().string(containsString("SmokeTest.testLogin")))
            .andExpect(content().string(containsString("SmokeTest.testLogout")))
            .andExpect(content().string(containsString("<!--end-sha-fieldset-->")))
            .andExpect(content().string(containsString("<!--end-branch-fieldset-->")));

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
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("<legend>Query Info</legend>")));
    }

    @Test
    void testAllHierarchyLevels_Org() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), eq("testco"), eq("testorg"), isNull(), isNull(), isNull()
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/company/testco/org/testorg/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/company/testco/org/testorg")));
    }

    @Test
    void testAllHierarchyLevels_Repo() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), eq("testco"), eq("testorg"), eq("testrepo"), isNull(), isNull()
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/company/testco/org/testorg/repo/testrepo/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/company/testco/org/testorg/repo/testrepo")));
    }

    @Test
    void testAllHierarchyLevels_Branch() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), eq("testco"), eq("testorg"), eq("testrepo"), eq("main"), isNull()
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/company/testco/org/testorg/repo/testrepo/branch/main/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/company/testco/org/testorg/repo/testrepo/branch/main")));
    }

    @Test
    void testAllHierarchyLevels_Sha() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), eq("testco"), eq("testorg"), eq("testrepo"), eq("main"), eq("abc123")
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/company/testco/org/testorg/repo/testrepo/branch/main/sha/abc123/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/company/testco/org/testorg/repo/testrepo/branch/main")));
    }

    @Test
    void testNoResults_ShowsMessage() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            eq("nonexistent"), eq("testco"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "nonexistent"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("No results found for this query")));
    }

    @Test
    void testMultipleBranchesAndShas() throws Exception {
        // Setup data with multiple branches and SHAs
        TagQueryResponse.JobResult job1 = TagQueryResponse.JobResult.builder()
            .runDate(Instant.parse("2024-01-15T10:30:00Z"))
            .tests(List.of("Test1", "Test2"))
            .build();

        TagQueryResponse.JobResult job2 = TagQueryResponse.JobResult.builder()
            .runDate(Instant.parse("2024-01-16T11:30:00Z"))
            .tests(List.of("Test3"))
            .build();

        // Branch 1, SHA 1
        Map<String, TagQueryResponse.JobResult> jobMap1 = new LinkedHashMap<>();
        jobMap1.put("job1", job1);

        Map<String, Map<String, TagQueryResponse.JobResult>> shaMap1 = new LinkedHashMap<>();
        shaMap1.put("sha1", jobMap1);

        // Branch 2, SHA 2
        Map<String, TagQueryResponse.JobResult> jobMap2 = new LinkedHashMap<>();
        jobMap2.put("job2", job2);

        Map<String, Map<String, TagQueryResponse.JobResult>> shaMap2 = new LinkedHashMap<>();
        shaMap2.put("sha2", jobMap2);

        Map<String, Map<String, Map<String, TagQueryResponse.JobResult>>> results = new LinkedHashMap<>();
        results.put("main", shaMap1);
        results.put("develop", shaMap2);

        when(tagQueryService.findByTagExpressionByPath(
            eq("smoke"), eq("testco"), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(results);

        mockMvc.perform(get("/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
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
            // Should be HTML-escaped in the error page
            .andExpect(content().string(containsString("&lt;script&gt;")));
    }
}
