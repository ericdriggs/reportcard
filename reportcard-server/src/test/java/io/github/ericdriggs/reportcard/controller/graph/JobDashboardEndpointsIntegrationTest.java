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
public class JobDashboardEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testJobDashboardUIEndpoint() throws Exception {
        mockMvc.perform(get("/job_dashboard/company/hulu/org/SubLife"))
                .andExpect(status().isOk());
    }

    @Test
    public void testJobDashboardUIEndpointWithJobInfo() throws Exception {
        mockMvc.perform(get("/job_dashboard/company/hulu/org/SubLife")
                .param("jobInfo", "pipeline:dev-cp3"))
                .andExpect(status().isOk());
    }

    @Test
    public void testJobDashboardUIEndpointWithDays() throws Exception {
        mockMvc.perform(get("/job_dashboard/company/hulu/org/SubLife")
                .param("days", "30"))
                .andExpect(status().isOk());
    }

    @Test
    public void testJobDashboardUIEndpointWithWildcard() throws Exception {
        mockMvc.perform(get("/job_dashboard/company/hulu/org/SubLife")
                .param("jobInfo", "pipeline:dev*"))
                .andExpect(status().isOk());
    }

    @Test
    public void testJobDashboardJsonEndpoint() throws Exception {
        mockMvc.perform(get("/v1/api/job_dashboard/company/hulu/org/SubLife"))
                .andExpect(status().isOk());
    }

    @Test
    public void testJobDashboardJsonEndpointWithJobInfo() throws Exception {
        mockMvc.perform(get("/v1/api/job_dashboard/company/hulu/org/SubLife")
                .param("jobInfo", "pipeline:dev-cp3"))
                .andExpect(status().isOk());
    }
}
