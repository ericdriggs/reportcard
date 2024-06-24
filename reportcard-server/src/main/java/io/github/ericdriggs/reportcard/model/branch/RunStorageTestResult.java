package io.github.ericdriggs.reportcard.model.branch;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.RunPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResultPojo;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.TreeSet;

@Builder
@Jacksonized
@Value
public class RunStorageTestResult {
    RunPojo runPojo;
    TestResultPojo testResultPojo;
    TreeSet<StoragePojo> storagePojos;


}
