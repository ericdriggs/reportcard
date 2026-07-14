package io.github.ericdriggs.reportcard.model.converter.karate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(access = AccessLevel.PACKAGE)
public class KarateNativeFeature {
    String packageQualifiedName;
    String relativePath;
    List<KarateNativeScenario> scenarios;
}
