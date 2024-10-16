package io.github.ericdriggs.reportcard.model.metrics.company;

import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.TreeMap;
import java.util.TreeSet;

@Builder
@Jacksonized
@Value
public class MetricsFilter implements Comparable<MetricsFilter> {

    @Builder.Default
    TreeSet<String> companies = new TreeSet<>();
    @Builder.Default
    TreeSet<String> orgs = new TreeSet<>();
    @Builder.Default
    TreeSet<String> repos = new TreeSet<>();
    @Builder.Default
    TreeSet<String> branches = new TreeSet<>();
    @Builder.Default
    TreeMap<String, TreeSet<String>> jobInfos = new TreeMap<>();

    @Override
    public int compareTo(MetricsFilter that) {
        return CompareUtil.chainCompare(
                CompareUtil.compareComparableCollection(companies, that.companies),
                CompareUtil.compareComparableCollection(orgs, that.orgs),
                CompareUtil.compareComparableCollection(repos, that.repos),
                CompareUtil.compareComparableCollection(branches, that.branches),
                CompareUtil.compareMultiValueStringMap(jobInfos, that.jobInfos)
        );
    }
}