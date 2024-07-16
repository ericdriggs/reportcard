package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.model.graph.RunGraph;
import io.github.ericdriggs.reportcard.persist.StorageType;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.net.URI;

@Builder
@Jacksonized
@Value
public class BadgeStatusUriStorageType {
    BadgeStatus badgeStatus;
    URI uri;
    StorageType storageType;
}
