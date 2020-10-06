package com.ericdriggs.reportcard.model;

import com.ericdriggs.reportcard.gen.db.tables.pojos.*;
import lombok.Data;

@Data
public class BuildStagePath {
    private Org org;
    private Repo repo;
    private App app;
    private Branch branch;
    private AppBranch appBranch;
    private Build build;
    private Stage stage;
    private BuildStage buildStage;

    public boolean isEmpty() {
        return org == null;
    }

    public boolean isComplete() {
        return org != null
                && repo != null
                && app != null
                && branch != null
                && appBranch != null
                && build != null
                && stage != null
                && buildStage != null;
    }
}