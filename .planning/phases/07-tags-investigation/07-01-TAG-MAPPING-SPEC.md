# Tag Mapping Specification: Karate JSON → test_result

## Karate JSON Tag Structure

Tags appear at two levels in Karate/Cucumber JSON:

### Feature Level (top-level array element)
```json
[
  {
    "tags": [
      {"name": "@envnot=staging-cp1-us-east-2", "line": 1},
      {"name": "@smoke", "line": 1}
    ],
    "name": "delorean-create-dss-test.feature",
    "elements": [...]
  }
]
```

### Scenario Level (elements array)
```json
{
  "elements": [
    {
      "tags": [
        {"name": "@smoke", "line": 3},
        {"name": "@env=staging", "line": 3}
      ],
      "name": "create dss account",
      "type": "scenario",
      "steps": [...]  // NOT stored - only name, tags, status extracted
    }
  ]
}
```

**Note:** `elements` = scenarios, `steps` = individual test steps. Steps are NOT stored in test_result JSON (too verbose). Only scenario-level metadata (name, tags, status, time) is extracted.

### JSON Paths
| Level | JSON Path | Example |
|-------|-----------|---------|
| Feature tags | `$[*].tags[*].name` | `"@smoke"` |
| Scenario tags | `$[*].elements[*].tags[*].name` | `"@env=staging"` |

## Tag Transformation Rules

### 1. Strip @ Prefix
```
Input:  "@smoke"           → Output: "smoke"
Input:  "@env=staging"     → Output: "env=staging"
Input:  "@envnot=staging"  → Output: "envnot=staging"
```

### 2. Preserve Key=Value Format
Key-value tags like `@env=staging` are stored as the literal string `"env=staging"` after @ removal. The `=` has no special meaning in storage.

### 3. Case Sensitivity
Tags are **case-sensitive** (MEMBER OF uses binary comparison). Lowercase is enforced by **convention** (team agreement), not application code.

### 4. Deduplication
All tags (from features and scenarios) are collected into a single deduplicated set per test_result.

## Target: test_result.tags Column

### Schema Change
```sql
ALTER TABLE test_result ADD COLUMN tags JSON;
```

### Stored Format
Union of all feature and scenario tags, deduplicated, @ stripped:
```json
["smoke", "regression", "env=staging", "browser=chrome"]
```

### Multi-Value Index
```sql
CREATE INDEX idx_test_result_tags ON test_result (
    (CAST(tags AS CHAR(100) ARRAY))
);
```

## Query Patterns

Simple MEMBER OF queries with index support:

```sql
-- Single tag (uses index)
SELECT * FROM test_result
WHERE 'smoke' MEMBER OF(tags);

-- OR semantics: use UNION (each leg uses index)
SELECT * FROM test_result WHERE 'smoke' MEMBER OF(tags)
UNION
SELECT * FROM test_result WHERE 'regression' MEMBER OF(tags);

-- AND semantics: single WHERE (uses index)
SELECT * FROM test_result
WHERE 'env=staging' MEMBER OF(tags)
  AND 'browser=chrome' MEMBER OF(tags);
```

## Extraction Logic

### Pseudocode
```java
public class KarateTagExtractor {

    /**
     * Extract all tags from a Karate JSON result.
     * Collects feature-level and scenario-level tags into one deduplicated set.
     */
    public List<String> extractAllTags(JsonNode karateJson) {
        Set<String> allTags = new LinkedHashSet<>();

        for (JsonNode feature : karateJson) {
            // Feature-level tags
            collectTags(feature.path("tags"), allTags);

            // Scenario-level tags
            for (JsonNode scenario : feature.path("elements")) {
                collectTags(scenario.path("tags"), allTags);
            }
        }

        return new ArrayList<>(allTags);
    }

    private void collectTags(JsonNode tagsArray, Set<String> target) {
        if (tagsArray == null || !tagsArray.isArray()) {
            return;
        }
        for (JsonNode tagObj : tagsArray) {
            String name = tagObj.path("name").asText("");
            if (!name.isEmpty()) {
                // Strip @ prefix only (case handled by convention)
                String tag = name.startsWith("@") ? name.substring(1) : name;
                target.add(tag);
            }
        }
    }
}
```

### Integration Point
When persisting a test result:

```java
// Extract tags from Karate JSON
List<String> tags = extractor.extractAllTags(karateJsonNode);

// Store on test_result entity
testResult.setTags(objectMapper.writeValueAsString(tags));
```

## Summary

| Source | Target | Transformation |
|--------|--------|----------------|
| `$[*].tags[*].name` | `test_result.tags` | Strip @ only, dedupe |
| `$[*].elements[*].tags[*].name` | `test_result.tags` | Strip @ only, dedupe |

**Key decisions:**
- Tags stored as JSON array column on `test_result` (not nested in test_suites_json)
- All feature + scenario tags flattened into single deduplicated array
- @ prefix stripped, case preserved as-is
- Key=value format preserved as literal string
- Case-sensitive matching (lowercase enforced by convention, not code)
- Multi-value index enables MEMBER OF queries
