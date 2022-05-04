package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import lombok.Data;

@Data
public class ExecutionStagePath {
    private Org org;
    private Repo repo;
    private Branch branch;
    private Sha sha;
    private Context context;
    private Execution execution;
    private Stage stage;


    public boolean isEmpty() {
        return org == null;
    }

    public boolean isComplete() {
        return org != null
                && repo != null
                && branch != null
                && sha != null
                && context != null
                && execution != null
                && stage != null
                ;
    }
}