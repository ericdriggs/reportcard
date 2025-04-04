//TODO: move from model to pojo
package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobInfoRunCountStageDTO;
import io.github.ericdriggs.reportcard.controller.util.ResponseServerUrl;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

//TOMAYBE: refactor to Value with Builder
@Data
public class StagePath implements Comparable<StagePath> {

    CompanyPojo company;
    OrgPojo org;
    RepoPojo repo;
    BranchPojo branch;
    JobPojo job;
    RunPojo run;
    StagePojo stage;

    @JsonIgnore
    public boolean isEmpty() {
        return org == null;
    }

    @JsonIgnore
    public boolean isComplete() {
        return validate().isEmpty();
    }

    @JsonIgnore
    public Map<String, String> validate() {

        //Prepare errors
        Map<String, String> validationErrors = new ConcurrentSkipListMap<>();
        if (ObjectUtils.isEmpty(org)) {
            validationErrors.put("org", "missing required field");
        }

        if (ObjectUtils.isEmpty(repo)) {
            validationErrors.put("repo", "missing required field");
        }

        if (ObjectUtils.isEmpty(branch)) {
            validationErrors.put("branch", "missing required field");
        }

        if (ObjectUtils.isEmpty(job)) {
            validationErrors.put("job", "missing required field");
        }

        if (ObjectUtils.isEmpty(run)) {
            validationErrors.put("run", "missing required field");
        }

        if (ObjectUtils.isEmpty(stage)) {
            validationErrors.put("stage", "missing required field");
        }

        return validationErrors;
    }

    @JsonIgnore
    public void updateLastRun(Instant lastRun) {
        if (lastRun == null) {
            throw new NullPointerException("lastRun");
        }

        if (branch != null) {
            branch.setLastRun(lastRun);
        }

        if (job != null) {
            job.setLastRun(lastRun);
        }


    }

    @JsonIgnore
    public void throwIfIncomplete() {
        Map<String, String> errors = validate();
        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());
        }
    }

    @JsonIgnore
    @Override
    public int compareTo(StagePath that) {
        return CompareUtil.chainCompare(
                ObjectUtils.compare(ObjectUtils.isEmpty(this), ObjectUtils.isEmpty(that)),
                PojoComparators.compareOrg(this.getOrg(), that.getOrg()),
                PojoComparators.compareRepo(this.getRepo(), that.getRepo()),
                PojoComparators.compareBranch(this.getBranch(), that.getBranch()),
                PojoComparators.compareJob(this.getJob(), that.getJob()),
                PojoComparators.compareRun(this.getRun(), that.getRun()),
                PojoComparators.compareStage(this.getStage(), that.getStage())
        );
    }

    //Lombok equals was considering object address which we do NOT want
    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof StagePath)) {
            return false;
        }
        return compareTo((StagePath)o) == 0;
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @JsonIgnore
    public Map<String,String> getUrlMaps() {
        final String serverUrl = ResponseServerUrl.getServerUrl();
        final CompanyOrgRepoBranchJobInfoRunCountStageDTO stage = CompanyOrgRepoBranchJobInfoRunCountStageDTO.fromStagePath(this);
        final CompanyOrgRepoBranchJobInfoRunCountStageDTO run = CompanyOrgRepoBranchJobInfoRunCountStageDTO.truncateRun(stage);
        TreeMap<String,String> urlMaps = new TreeMap<>();
        urlMaps.put("run", serverUrl + run.toUrlPath());
        urlMaps.put("stage", serverUrl + stage.toUrlPath());
        return urlMaps;
    }
}