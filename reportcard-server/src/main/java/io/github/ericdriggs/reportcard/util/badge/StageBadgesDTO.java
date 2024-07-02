package io.github.ericdriggs.reportcard.util.badge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.controller.graph.TrendHtmlHelper;
import io.github.ericdriggs.reportcard.model.graph.StageGraph;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ObjectUtils;

import java.net.URI;
import java.util.TreeSet;

@Builder
@Jacksonized
@Value
public class StageBadgesDTO implements Comparable<StageBadgesDTO> {
    Long stageId;
    String stageName;
    BadgeStatus badgeStatus;
    URI stageUri;
    URI trendUri;

    @Builder.Default
    TreeSet<StorageUri> storageUris = new TreeSet<>();

    @Override
    public int compareTo(StageBadgesDTO that) {

        return CompareUtil.chainCompare(
                CompareUtil.compareLong(this.stageId, that.stageId),
                CompareUtil.compareLowerNullSafe(this.stageName, that.stageName),
                ObjectUtils.compare(this.badgeStatus, that.badgeStatus),
                CompareUtil.compareNullSafe(this.stageUri, that.stageUri),
                CompareUtil.compareNullSafe(this.trendUri, that.trendUri)
        );
    }

    public static StageBadgesDTO fromStageGraph(StageGraph stageGraph, CompanyOrgRepoBranchJobRunStageDTO path, TreeSet<StorageUri> storageUris) {
        final String stageUriString =  path.toUrlPath();
        final URI trendUri = TrendHtmlHelper.getTrendURI(path);
        return StageBadgesDTO
                .builder()
                .stageId(stageGraph.stageId())
                .stageName(stageGraph.stageName())
                .badgeStatus(BadgeStatus.fromStageGraph(stageGraph))
                .stageUri(URI.create(stageUriString))
                .trendUri(TrendHtmlHelper.getTrendURI(path))
                .storageUris(storageUris)
                .build();
    }

    @JsonIgnore
    public BadgeStatusUri toBadgeStatusUri() {
        return BadgeStatusUri.builder()
                             .badgeStatus(badgeStatus)
                             .uri(stageUri)
                             .build();
    }
}
