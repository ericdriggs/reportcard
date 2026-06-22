package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.model.failures.FailuresDashboardRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class FailuresDashboardRequestHelper {

    public static FailuresDashboardRequest buildRequest(
            String company, String org, Integer days, Integer failureThreshold,
            List<String> repos, List<String> jobInfo, Integer limit) {

        FailuresDashboardRequest.FailuresDashboardRequestBuilder builder = FailuresDashboardRequest.builder()
                .company(company)
                .org(org)
                .days(days)
                .minFailurePercent(failureThreshold)
                .repos(repos)
                .limit(limit);

        if (jobInfo != null && !jobInfo.isEmpty()) {
            String[] parts = jobInfo.get(0).split(":", 2);
            if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                builder.jobInfoKey(parts[0])
                        .jobInfoValues(List.of(parts[1].split(",")));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "jobInfo must be in format 'key:value1,value2'. Got: " + jobInfo.get(0));
            }
        }

        return builder.build();
    }
}
