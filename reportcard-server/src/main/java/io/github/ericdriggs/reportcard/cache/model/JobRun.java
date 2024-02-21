package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Run;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class JobRun {

    Job job;
    Run run;

    public boolean isSuccess() {
        if (run == null || run.getIsSuccess() == null) {
            return false;
        }

        return run.getIsSuccess();
    }
}
