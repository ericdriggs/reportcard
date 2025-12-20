package io.github.ericdriggs.reportcard.model.pipeline;

import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.TreeMap;
import java.util.TreeSet;

@Builder
@Jacksonized
@Value
public class PipelineRequest implements Comparable<PipelineRequest> {
    TreeSet<String> companies;
    TreeSet<String> orgs;
    TreeSet<String> repos;
    TreeSet<String> branches;
    TreeSet<String> jobInfos;
    TreeSet<String> notCompanies;
    TreeSet<String> notOrgs;
    TreeSet<String> notRepos;
    TreeSet<String> notBranches;
    TreeSet<String> notJobInfos;
    boolean shouldIncludeDefaultBranches;
    InstantRange range;

    @Override
    public int compareTo(PipelineRequest that) {
        return this.range.compareTo(that.range);
    }

    public TreeMap<String, TreeSet<String>> getRequiredJobInfos() {
        TreeMap<String, TreeSet<String>> required = new TreeMap<>();
        if (jobInfos != null && !jobInfos.isEmpty()) {
            for (String jobInfo : jobInfos) {
                String[] parts = jobInfo.split(":");
                if (parts.length == 2) {
                    required.computeIfAbsent(parts[0], k -> new TreeSet<>()).add(parts[1]);
                }
            }
        }
        return required;
    }

    public TreeMap<String, TreeSet<String>> getExcludedJobInfos() {
        TreeMap<String, TreeSet<String>> excluded = new TreeMap<>();
        if (notJobInfos != null && !notJobInfos.isEmpty()) {
            for (String jobInfo : notJobInfos) {
                String[] parts = jobInfo.split(":");
                if (parts.length == 2) {
                    excluded.computeIfAbsent(parts[0], k -> new TreeSet<>()).add(parts[1]);
                }
            }
        }
        return excluded;
    }
}