package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Company;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class CompanyOrgRepoBranch {
    Company company;
    Org org;
    Repo repo;
    Branch branch;
}
