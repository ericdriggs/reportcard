package io.github.ericdriggs.reportcard.cache.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.PojoComparators;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Storage;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
public class TestResultStorages {

    TestResult testResult;
    @Builder.Default
    Set<Storage> storages = new TreeSet<>(PojoComparators.STORAGE_CASE_INSENSITIVE_ORDER);

}
