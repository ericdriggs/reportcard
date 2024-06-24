package io.github.ericdriggs.reportcard.controller.graph.trend;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;
import java.util.TreeMap;

@Builder
@Jacksonized
@Value
public class FailureMessageIndexMap {

    @Builder.Default
    TreeMap<String, Integer> failureMessageIndexMap = new TreeMap<>();

    public Integer getFailureIndex(String failureMessage) {
        if (failureMessage == null) {
            failureMessage = "null";
        }
        failureMessageIndexMap.computeIfAbsent(failureMessage, k -> failureMessageIndexMap.size() + 1);
        return failureMessageIndexMap.get(failureMessage);
    }

    //reverse the map since we want it to be ordered numerically for display
    public TreeMap<Integer, String> getIndexFailureMessageMap() {
        TreeMap<Integer,String> indexMap = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : failureMessageIndexMap.entrySet()) {
            indexMap.put(entry.getValue(), entry.getKey());
        }
        return indexMap;
    }
}
