package io.github.ericdriggs.reportcard.model.graph;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder.Options(useImmutableCollections = true)
@RecordBuilder

public record TestCaseFaultGraph(
        Long testCaseFaultId,
        Long testCaseFk,
        Byte faultContextFk,
        String type,
        String message,
        String value
) implements TestCaseFaultGraphBuilder.With {
}
