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
Tags are **case-sensitive** (MEMBER OF uses binary comparison). Convention: normalize to lowercase during ingestion.

```
Input:  "@Smoke"    → Output: "smoke" (normalized)
Input:  "@ENV=Prod" → Output: "env=prod" (normalized)
```

### 4. Deduplication
If a tag appears multiple times (e.g., inherited from feature to scenario), store once per level.

## Target Model Mapping

### Current Model Structure
```
TestResult
  └── testSuitesJson: String (JSON array of TestSuiteModel)
        └── TestSuiteModel (Feature)
              ├── name, tests, error, failure, etc.
              └── testCases: List<TestCaseModel> (Scenarios)
                    └── TestCaseModel
                          └── name, className, time, etc.
```

### Proposed Model Changes

**TestSuite/TestSuiteModel** - add:
```java
private List<String> tags;  // Feature-level tags, @ stripped, lowercase
```

**TestCase/TestCaseModel** - add:
```java
private List<String> tags;  // Scenario-level tags, @ stripped, lowercase
```

### Resulting JSON Structure
```json
[
  {
    "name": "delorean-create-dss-test.feature",
    "tags": ["smoke", "regression"],
    "tests": 5,
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

## Extraction Logic

### Pseudocode
```java
public class KarateTagExtractor {

    public List<String> extractTags(JsonNode tagsArray) {
        if (tagsArray == null || !tagsArray.isArray()) {
            return List.of();
        }

        Set<String> uniqueTags = new LinkedHashSet<>();
        for (JsonNode tagObj : tagsArray) {
            String name = tagObj.path("name").asText("");
            if (!name.isEmpty()) {
                // Strip @ prefix and normalize to lowercase
                String normalized = name.startsWith("@")
                    ? name.substring(1).toLowerCase(Locale.ROOT)
                    : name.toLowerCase(Locale.ROOT);
                uniqueTags.add(normalized);
            }
        }
        return new ArrayList<>(uniqueTags);
    }
}
```

### Integration Point
In existing Karate JSON parsing (likely in model mappers or converters):

```java
// When parsing feature JSON
TestSuiteModel suite = new TestSuiteModel();
suite.setName(featureNode.path("name").asText());
suite.setTags(extractTags(featureNode.path("tags")));  // NEW

// When parsing scenario JSON
TestCaseModel testCase = new TestCaseModel();
testCase.setName(scenarioNode.path("name").asText());
testCase.setTags(extractTags(scenarioNode.path("tags")));  // NEW
```

## Query Patterns

Once tags are stored, queries use MEMBER OF with multi-value index:

```sql
-- Find tests with "smoke" tag (feature OR scenario level)
SELECT * FROM test_result
WHERE 'smoke' MEMBER OF(
    JSON_EXTRACT(test_suites_json, '$[*].tags')
)
UNION
SELECT * FROM test_result
WHERE 'smoke' MEMBER OF(
    JSON_EXTRACT(test_suites_json, '$[*].testCases[*].tags')
);
```

**Note:** The nested JSON path `$[*].testCases[*].tags` may require flattening for efficient indexing. Consider extracting all tags to a top-level `tags` field on `test_result` for simpler queries.

## Alternative: Flattened Tags on test_result

For simpler querying, consider adding a denormalized `tags` column directly on `test_result`:

```sql
ALTER TABLE test_result ADD COLUMN tags JSON;
-- Contains union of all feature and scenario tags for this result
-- ["smoke", "regression", "env=staging", "browser=chrome"]
```

This trades storage for query simplicity:
```sql
-- Simple query without nested JSON paths
SELECT * FROM test_result
WHERE 'smoke' MEMBER OF(tags);
```

**Tradeoff:** Loses distinction between feature-level and scenario-level tags, but simplifies most common queries.

## Summary

| Source | Target | Transformation |
|--------|--------|----------------|
| `$[*].tags[*].name` | `TestSuiteModel.tags` | Strip @, lowercase |
| `$[*].elements[*].tags[*].name` | `TestCaseModel.tags` | Strip @, lowercase |

**Key decisions:**
- Tags stored as `List<String>` in model, serialized to JSON array
- @ prefix stripped, normalized to lowercase
- Key=value format preserved as literal string
- Case-sensitive matching (convention enforces lowercase)
