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

    private Integer companyId;
    private String companyName;
    private List<OrgReposEntry> orgs;

    /**
     * Entry containing org fields and its repositories.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrgReposEntry {
        private Integer orgId;
        private String orgName;
        private Integer companyFk;
        private List<RepoEntry> repos;

        public static OrgReposEntry fromPojo(OrgPojo pojo, List<RepoEntry> repos) {
            if (pojo == null) {
                return null;
            }
            return OrgReposEntry.builder()
                    .orgId(pojo.getOrgId())
                    .orgName(pojo.getOrgName())
                    .companyFk(pojo.getCompanyFk())
                    .repos(repos)
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

                orgs.add(OrgReposEntry.fromPojo(orgPojo, repos));
            }
        }

        return CompanyOrgsReposResponse.builder()
                .companyId(companyPojo.getCompanyId())
                .companyName(companyPojo.getCompanyName())
                .orgs(orgs)
                .build();
    }
}
