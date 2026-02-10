package io.github.ericdriggs.reportcard.controller;

import io.github.ericdriggs.reportcard.persist.tags.ParseException;
import io.github.ericdriggs.reportcard.persist.tags.TagQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TagQueryController using MockMvc.
 *
 * <p>Tests verify REST API contract including:
 * - Each hierarchy level endpoint
 * - Query parameter parsing
 * - Error responses for invalid queries
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
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/api/v1/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("testco"))
            .andExpect(jsonPath("$.query.tags").value("smoke"));
    }

    @Test
    void searchByTags_orgLevel_returnsOk() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), anyString(), isNull(), isNull(), isNull()
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/api/v1/company/testco/org/testorg/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("testco/testorg"))
            .andExpect(jsonPath("$.query.tags").value("smoke"));
    }

    @Test
    void searchByTags_repoLevel_returnsOk() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), anyString(), anyString(), isNull(), isNull()
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/api/v1/company/testco/org/testorg/repo/testrepo/tags/tests")
                .param("tags", "smoke AND env=prod"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("testco/testorg/testrepo"))
            .andExpect(jsonPath("$.query.tags").value("smoke AND env=prod"));
    }

    @Test
    void searchByTags_branchLevel_returnsOk() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), anyString(), anyString(), anyString(), isNull()
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/api/v1/company/testco/org/testorg/repo/testrepo/branch/main/tags/tests")
                .param("tags", "regression"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("testco/testorg/testrepo/main"))
            .andExpect(jsonPath("$.query.tags").value("regression"));
    }

    @Test
    void searchByTags_shaLevel_returnsOk() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(new LinkedHashMap<>());

        mockMvc.perform(get("/api/v1/company/co/org/o/repo/r/branch/b/sha/s/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.query.scope").value("co/o/r/b/s"));
    }

    @Test
    void searchByTags_missingTagsParam_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/company/testco/tags/tests"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchByTags_invalidExpression_returns400() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), isNull(), isNull(), isNull(), isNull()
        )).thenThrow(new ParseException("Unexpected token", 0));

        mockMvc.perform(get("/api/v1/company/testco/tags/tests")
                .param("tags", "AND smoke"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid tag expression"));
    }

    @Test
    void searchByTags_complexExpression_acceptsUrlEncodedSyntax() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(new LinkedHashMap<>());

        // URL-encoded: (smoke OR regression) AND env=prod
        mockMvc.perform(get("/api/v1/company/testco/tags/tests")
                .param("tags", "(smoke OR regression) AND env=prod"))
            .andExpect(status().isOk());
    }

    @Test
    void searchByTags_emptyExpression_returns400() throws Exception {
        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), isNull(), isNull(), isNull(), isNull()
        )).thenThrow(new ParseException("Empty expression"));

        mockMvc.perform(get("/api/v1/company/testco/tags/tests")
                .param("tags", ""))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid tag expression"));
    }

    @Test
    void searchByTags_serviceReturnsResults_includesInResponse() throws Exception {
        // Setup mock to return some results
        var branchResults = new LinkedHashMap<String, io.github.ericdriggs.reportcard.model.TagQueryResponse.JobResult>();
        branchResults.put("default", io.github.ericdriggs.reportcard.model.TagQueryResponse.JobResult.builder()
            .tests(java.util.List.of("test1", "test2"))
            .build());

        var shaResults = new LinkedHashMap<String, java.util.Map<String, io.github.ericdriggs.reportcard.model.TagQueryResponse.JobResult>>();
        shaResults.put("abc123", branchResults);

        var results = new LinkedHashMap<String, java.util.Map<String, java.util.Map<String, io.github.ericdriggs.reportcard.model.TagQueryResponse.JobResult>>>();
        results.put("main", shaResults);

        when(tagQueryService.findByTagExpressionByPath(
            anyString(), anyString(), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(results);

        mockMvc.perform(get("/api/v1/company/testco/tags/tests")
                .param("tags", "smoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results.main.abc123.default.tests[0]").value("test1"))
            .andExpect(jsonPath("$.results.main.abc123.default.tests[1]").value("test2"));
    }
}
