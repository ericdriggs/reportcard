package io.github.ericdriggs.reportcard.model.converter.karate;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Extracts and transforms tags from Karate JSON format.
 *
 * <p>Transformation rules:
 * <ul>
 *   <li>Strip @ prefix from all tags</li>
 *   <li>Remove all whitespace from tags</li>
 *   <li>Expand comma-separated values with prefix propagation</li>
 *   <li>Deduplicate tags while preserving order</li>
 * </ul>
 */
public class KarateTagExtractor {

    /**
     * Extracts tags from a Karate JSON tags array.
     *
     * @param tagsArray JsonNode containing tag objects with "name" field
     * @return deduplicated list of normalized tag strings, empty if null/invalid input
     */
    public List<String> extractTags(JsonNode tagsArray) {
        if (tagsArray == null || !tagsArray.isArray()) {
            return List.of();
        }

        Set<String> tags = new LinkedHashSet<>();
        for (JsonNode tagObj : tagsArray) {
            String name = tagObj.path("name").asText("");
            if (!name.isEmpty()) {
                tags.addAll(expandTag(name));
            }
        }
        return new ArrayList<>(tags);
    }

    /**
     * Expands a single tag string, handling @ prefix, whitespace, and comma expansion.
     *
     * @param raw raw tag string from Karate JSON
     * @return list of expanded tags (single element if no comma, multiple if comma-separated)
     * @throws IllegalArgumentException if comma present without = in first part
     */
    private List<String> expandTag(String raw) {
        // Strip @ prefix if present
        String tag = raw.startsWith("@") ? raw.substring(1) : raw;

        // Remove all whitespace
        tag = tag.replaceAll("\\s+", "");

        // Split on comma
        String[] parts = tag.split(",");

        // Single part - return as-is
        if (parts.length == 1) {
            return List.of(tag);
        }

        // Multiple parts - validate and expand
        String firstPart = parts[0];
        int eqIndex = firstPart.indexOf('=');
        if (eqIndex < 0) {
            throw new IllegalArgumentException("Comma without =: " + raw);
        }

        // Extract prefix (everything up to and including first =)
        String prefix = firstPart.substring(0, eqIndex + 1);

        List<String> expanded = new ArrayList<>();
        for (String part : parts) {
            if (part.contains("=")) {
                expanded.add(part);
            } else {
                expanded.add(prefix + part);
            }
        }
        return expanded;
    }
}
