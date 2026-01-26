package io.github.ericdriggs.reportcard.controller.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility for parsing jobInfo query parameters in graph controllers.
 * Package-private since it's only used by GraphJsonController and GraphUIController.
 * Converts user wildcard syntax (*) to SQL wildcard syntax (%).
 */
class JobInfoParser {

    /**
     * Parse jobInfo parameters into a map.
     * Format: "key:value" where * is converted to % for SQL LIKE queries.
     * Invalid formats are silently ignored.
     *
     * @param jobInfo List of "key:value" strings
     * @return Map of key-value pairs with wildcards converted
     */
    public static Map<String, String> parseJobInfoParams(List<String> jobInfo) {
        Map<String, String> jobInfoMap = new HashMap<>();
        if (jobInfo != null) {
            for (String info : jobInfo) {
                String[] parts = info.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (value.contains("*")) {
                        value = value.replace("*", "%");
                    }
                    jobInfoMap.put(key, value);
                }
            }
        }
        return jobInfoMap;
    }
}
