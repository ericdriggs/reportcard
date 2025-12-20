package io.github.ericdriggs.reportcard.controller.graph;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class PipelineEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPipelineAllEndpoint() throws Exception {
        mockMvc.perform(get("/v1/api/pipeline/all")
                .param("intervalDays", "7")
                .param("intervalCount", "4"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPipelineCompanyEndpoint() throws Exception {
        mockMvc.perform(get("/v1/api/pipeline/company/testcompany")
                .param("intervalDays", "7")
                .param("intervalCount", "4"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPipelineOrgEndpoint() throws Exception {
        mockMvc.perform(get("/v1/api/pipeline/company/testcompany/org/testorg")
                .param("intervalDays", "7")
                .param("intervalCount", "4"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPipelineUIAllEndpoint() throws Exception {
        mockMvc.perform(get("/ui/pipeline/all")
                .param("intervalDays", "7")
                .param("intervalCount", "4"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPipelineUICompanyEndpoint() throws Exception {
        mockMvc.perform(get("/ui/pipeline/company/testcompany")
                .param("intervalDays", "7")
                .param("intervalCount", "4"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPipelineUIOrgEndpoint() throws Exception {
        mockMvc.perform(get("/ui/pipeline/company/testcompany/org/testorg")
                .param("intervalDays", "7")
                .param("intervalCount", "4"))
                .andExpect(status().isOk());
    }
}