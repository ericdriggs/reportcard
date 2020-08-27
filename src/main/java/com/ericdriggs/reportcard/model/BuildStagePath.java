package com.ericdriggs.reportcard.model;

import com.ericdriggs.reportcard.db.tables.pojos.*;
import lombok.Data;

@Data
public class BuildStagePath {
    private Org org;
    private Repo repo;
    private App app;
    private Branch branch;
    private Build build;
    private Stage stage;

}