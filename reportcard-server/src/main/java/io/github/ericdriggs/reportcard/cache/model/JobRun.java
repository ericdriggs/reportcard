package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.JobPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class JobRun {

    JobPojo job;
    RunPojo run;

    public boolean isSuccess() {
        if (run == null || run.getIsSuccess() == null) {
            return false;
        }

        return run.getIsSuccess();
    }
}
