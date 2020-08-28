package com.ericdriggs.reportcard.model;

import com.ericdriggs.reportcard.db.tables.pojos.*;
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

    private boolean isComplete() {
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