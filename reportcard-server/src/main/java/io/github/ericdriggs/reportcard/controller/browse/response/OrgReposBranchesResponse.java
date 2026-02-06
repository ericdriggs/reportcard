package io.github.ericdriggs.reportcard.controller.browse.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.BranchPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Response DTO for /company/{company}/org/{org} endpoint.
 * Transforms Map&lt;OrgPojo, Map&lt;RepoPojo, Set&lt;BranchPojo&gt;&gt;&gt; into clean nested JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgReposBranchesResponse {

    private OrgEntry org;
    private List<RepoBranchesEntry> repos;

    /**
     * Organization fields copied from OrgPojo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrgEntry {
        private Integer orgId;
        private String orgName;
        private Integer companyFk;

        public static OrgEntry fromPojo(OrgPojo pojo) {
            if (pojo == null) {
                return null;
            }
            return OrgEntry.builder()
                    .orgId(pojo.getOrgId())
                    .orgName(pojo.getOrgName())
                    .companyFk(pojo.getCompanyFk())
                    .build();
        }
    }

    /**
     * Entry containing a repository and its branches.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RepoBranchesEntry {
        private RepoEntry repo;
        private List<BranchEntry> branches;
    }

    /**
     * Repository fields copied from RepoPojo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RepoEntry {
        private Integer repoId;
        private String repoName;
        private Integer orgFk;

        public static RepoEntry fromPojo(RepoPojo pojo) {
            if (pojo == null) {
                return null;
            }
            return RepoEntry.builder()
                    .repoId(pojo.getRepoId())
                    .repoName(pojo.getRepoName())
                    .orgFk(pojo.getOrgFk())
                    .build();
        }
    }

    /**
     * Branch fields copied from BranchPojo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BranchEntry {
        private Integer branchId;
        private String branchName;
        private Integer repoFk;
        private Instant lastRun;

        public static BranchEntry fromPojo(BranchPojo pojo) {
            if (pojo == null) {
                return null;
            }
            return BranchEntry.builder()
                    .branchId(pojo.getBranchId())
                    .branchName(pojo.getBranchName())
                    .repoFk(pojo.getRepoFk())
                    .lastRun(pojo.getLastRun())
                    .build();
        }
    }

    /**
     * Factory method to transform cache Map to DTO.
     * Note: Input map typically has single org key (filtered by controller path).
     *
     * @param map the cache Map&lt;OrgPojo, Map&lt;RepoPojo, Set&lt;BranchPojo&gt;&gt;&gt;
     * @return OrgReposBranchesResponse DTO
     */
    public static OrgReposBranchesResponse fromMap(Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>> map) {
        if (map == null || map.isEmpty()) {
            return OrgReposBranchesResponse.builder()
                    .repos(new ArrayList<>())
                    .build();
        }

        // Take first entry (single org expected from filtered endpoint)
        Map.Entry<OrgPojo, Map<RepoPojo, Set<BranchPojo>>> orgEntry = map.entrySet().iterator().next();
        OrgPojo orgPojo = orgEntry.getKey();
        Map<RepoPojo, Set<BranchPojo>> repoBranchesMap = orgEntry.getValue();

        List<RepoBranchesEntry> repos = new ArrayList<>();
        if (repoBranchesMap != null) {
            for (Map.Entry<RepoPojo, Set<BranchPojo>> repoEntry : repoBranchesMap.entrySet()) {
                RepoPojo repoPojo = repoEntry.getKey();
                Set<BranchPojo> branchPojos = repoEntry.getValue();

                List<BranchEntry> branches = new ArrayList<>();
                if (branchPojos != null) {
                    for (BranchPojo branchPojo : branchPojos) {
                        branches.add(BranchEntry.fromPojo(branchPojo));
                    }
                }

                RepoBranchesEntry repoBranchesEntry = RepoBranchesEntry.builder()
                        .repo(RepoEntry.fromPojo(repoPojo))
                        .branches(branches)
                        .build();
                repos.add(repoBranchesEntry);
            }
        }

        return OrgReposBranchesResponse.builder()
                .org(OrgEntry.fromPojo(orgPojo))
                .repos(repos)
                .build();
    }
}
