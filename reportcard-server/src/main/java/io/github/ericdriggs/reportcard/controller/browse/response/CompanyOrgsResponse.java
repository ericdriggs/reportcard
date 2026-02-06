package io.github.ericdriggs.reportcard.controller.browse.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Response DTO for /v1/api endpoint.
 * Transforms Map&lt;CompanyPojo, Set&lt;OrgPojo&gt;&gt; into clean nested JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyOrgsResponse {

    private List<CompanyEntry> companies;

    /**
     * Entry containing a company and its organizations.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CompanyEntry {
        private Company company;
        private List<OrgEntry> orgs;
    }

    /**
     * Company fields copied from CompanyPojo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Company {
        private Integer companyId;
        private String companyName;

        public static Company fromPojo(CompanyPojo pojo) {
            if (pojo == null) {
                return null;
            }
            return Company.builder()
                    .companyId(pojo.getCompanyId())
                    .companyName(pojo.getCompanyName())
                    .build();
        }
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
     * Factory method to transform cache Map to DTO.
     *
     * @param map the cache Map&lt;CompanyPojo, Set&lt;OrgPojo&gt;&gt;
     * @return CompanyOrgsResponse DTO
     */
    public static CompanyOrgsResponse fromMap(Map<CompanyPojo, Set<OrgPojo>> map) {
        if (map == null) {
            return CompanyOrgsResponse.builder().companies(new ArrayList<>()).build();
        }

        List<CompanyEntry> companies = new ArrayList<>();
        for (Map.Entry<CompanyPojo, Set<OrgPojo>> entry : map.entrySet()) {
            CompanyPojo companyPojo = entry.getKey();
            Set<OrgPojo> orgPojos = entry.getValue();

            List<OrgEntry> orgs = new ArrayList<>();
            if (orgPojos != null) {
                for (OrgPojo orgPojo : orgPojos) {
                    orgs.add(OrgEntry.fromPojo(orgPojo));
                }
            }

            CompanyEntry companyEntry = CompanyEntry.builder()
                    .company(Company.fromPojo(companyPojo))
                    .orgs(orgs)
                    .build();
            companies.add(companyEntry);
        }

        return CompanyOrgsResponse.builder()
                .companies(companies)
                .build();
    }
}
