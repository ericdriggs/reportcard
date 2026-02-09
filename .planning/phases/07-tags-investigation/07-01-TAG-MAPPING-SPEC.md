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
Remove leading `@` from all tags.

### 2. Expand Comma-Separated Values
Split on `,` only. If the first part contains `=`, propagate that prefix to bare parts:
```
@env=dev,test     → ["env=dev", "env=test"]   (prefix "env=" propagates to "test")
@env=dev, test    → ["env=dev", "env=test"]   (trim whitespace)
@env=staging      → ["env=staging"]           (no comma, no expansion)
@foo=bar=baz      → ["foo=bar=baz"]           (no comma, stored as-is)
```

Comma without `=` is invalid:
```
@smoke            → ["smoke"]
@smoke,regression → INVALID
```

### 3. Case Sensitivity
Tags are **case-sensitive** (MEMBER OF uses binary comparison). Lowercase is enforced by **convention** (team agreement), not application code.

### 4. Deduplication
All tags (from features and scenarios) are collected into a single deduplicated set per test_result.

## Target Model: Three Levels

Tags are stored at three levels:

### 1. test_result.tags (for indexing/querying)
```sql
ALTER TABLE test_result ADD COLUMN tags JSON;
```
Flattened union of all feature + scenario tags, deduplicated:
```json
["smoke", "regression", "env=staging", "browser=chrome"]
```

Multi-value index:
```sql
CREATE INDEX idx_test_result_tags ON test_result (
    (CAST(tags AS CHAR(100) ARRAY))
);
```

### 2. TestSuiteModel.tags (feature-level, in test_suites_json)
```java
private List<String> tags;  // Feature-level tags, @ stripped
```

### 3. TestCaseModel.tags (scenario-level, in test_suites_json)
```java
private List<String> tags;  // Scenario-level tags, @ stripped
```

### Resulting test_suites_json Structure
```json
[
  {
    "name": "delorean-create-dss-test.feature",
    "tags": ["smoke", "regression"],
    "testCases": [
      {
        "name": "create dss account",
        "tags": ["smoke", "env=staging"],
        "time": 19.3
      }
    ]
  }
]
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
     * Strip @, split on comma, propagate prefix to bare parts.
     */
    private List<String> expandTag(String raw) {
        String tag = raw.startsWith("@") ? raw.substring(1) : raw;
        String[] parts = tag.split(",");

        if (parts.length == 1) {
            return List.of(tag.trim());
        }

        // Find prefix (everything up to and including first =)
        String firstPart = parts[0].trim();
        int eqIndex = firstPart.indexOf('=');
        if (eqIndex < 0) {
            throw new IllegalArgumentException("Comma without =: " + raw);
        }
        String prefix = firstPart.substring(0, eqIndex + 1);

        List<String> expanded = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.contains("=")) {
                expanded.add(trimmed);
            } else {
                expanded.add(prefix + trimmed);
            }
        }
        return expanded;
    }
}
```

### Integration Point
When parsing Karate JSON, populate all three levels:

```java
Set<String> allTags = new LinkedHashSet<>();  // For test_result.tags

for (JsonNode feature : karateJson) {
    TestSuiteModel suite = new TestSuiteModel();

    // Level 2: Feature tags
    List<String> featureTags = extractor.extractTags(feature.path("tags"));
    suite.setTags(featureTags);
    allTags.addAll(featureTags);

    for (JsonNode scenario : feature.path("elements")) {
        TestCaseModel testCase = new TestCaseModel();

        // Level 3: Scenario tags
        List<String> scenarioTags = extractor.extractTags(scenario.path("tags"));
        testCase.setTags(scenarioTags);
        allTags.addAll(scenarioTags);
    }
}

// Level 1: Flattened tags for indexing
testResult.setTags(objectMapper.writeValueAsString(new ArrayList<>(allTags)));
```

## Summary

| Source | Target | Purpose |
|--------|--------|---------|
| `$[*].tags[*].name` | `TestSuiteModel.tags` | Feature-level display |
| `$[*].elements[*].tags[*].name` | `TestCaseModel.tags` | Scenario-level display |
| All of the above | `test_result.tags` | Flattened for indexing |

**Key decisions:**
- Tags stored at 3 levels: test_result.tags (indexed), TestSuiteModel.tags, TestCaseModel.tags
- test_result.tags: flattened union of all tags, deduplicated, for MEMBER OF queries
- Nested tags in test_suites_json: preserve feature/scenario attribution
- @ prefix stripped, case preserved as-is
- Key=value format preserved as literal string
- Case-sensitive matching (lowercase enforced by convention, not code)
- Multi-value index on test_result.tags enables MEMBER OF queries
