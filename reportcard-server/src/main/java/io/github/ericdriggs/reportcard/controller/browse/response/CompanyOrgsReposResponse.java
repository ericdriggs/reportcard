package io.github.ericdriggs.reportcard.controller.browse.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RepoPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Response DTO for /company/{company} endpoint.
 * Transforms Map&lt;CompanyPojo, Map&lt;OrgPojo, Set&lt;RepoPojo&gt;&gt;&gt; into clean nested JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyOrgsReposResponse {

    private CompanyEntry company;
    private List<OrgReposEntry> orgs;

    /**
     * Company fields copied from CompanyPojo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CompanyEntry {
        private Integer companyId;
        private String companyName;

        public static CompanyEntry fromPojo(CompanyPojo pojo) {
            if (pojo == null) {
                return null;
            }
            return CompanyEntry.builder()
                    .companyId(pojo.getCompanyId())
                    .companyName(pojo.getCompanyName())
                    .build();
        }
    }

    /**
     * Entry containing an organization and its repositories.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrgReposEntry {
        private OrgEntry org;
        private List<RepoEntry> repos;
    }

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
     * Factory method to transform cache Map to DTO.
     * Note: Input map typically has single company key (filtered by controller path).
     *
     * @param map the cache Map&lt;CompanyPojo, Map&lt;OrgPojo, Set&lt;RepoPojo&gt;&gt;&gt;
     * @return CompanyOrgsReposResponse DTO
     */
    public static CompanyOrgsReposResponse fromMap(Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>> map) {
        if (map == null || map.isEmpty()) {
            return CompanyOrgsReposResponse.builder()
                    .orgs(new ArrayList<>())
                    .build();
        }

        // Take first entry (single company expected from filtered endpoint)
        Map.Entry<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>> companyEntry = map.entrySet().iterator().next();
        CompanyPojo companyPojo = companyEntry.getKey();
        Map<OrgPojo, Set<RepoPojo>> orgReposMap = companyEntry.getValue();

        List<OrgReposEntry> orgs = new ArrayList<>();
        if (orgReposMap != null) {
            for (Map.Entry<OrgPojo, Set<RepoPojo>> orgEntry : orgReposMap.entrySet()) {
                OrgPojo orgPojo = orgEntry.getKey();
                Set<RepoPojo> repoPojos = orgEntry.getValue();

                List<RepoEntry> repos = new ArrayList<>();
                if (repoPojos != null) {
                    for (RepoPojo repoPojo : repoPojos) {
                        repos.add(RepoEntry.fromPojo(repoPojo));
                    }
                }

                OrgReposEntry orgReposEntry = OrgReposEntry.builder()
                        .org(OrgEntry.fromPojo(orgPojo))
                        .repos(repos)
                        .build();
                orgs.add(orgReposEntry);
            }
        }

        return CompanyOrgsReposResponse.builder()
                .company(CompanyEntry.fromPojo(companyPojo))
                .orgs(orgs)
                .build();
    }
}
