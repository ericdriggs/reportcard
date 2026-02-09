# Phase 7: Tags Investigation - Research

**Researched:** 2026-02-09
**Domain:** MySQL 8.0 JSON indexing for tag-based search
**Confidence:** HIGH

## Summary

MySQL 8.0 provides four indexing strategies for JSON columns: functional indexes, generated columns, multi-value indexes (8.0.17+), and junction tables (normalized). For the tag search use cases defined (OR list on keys, OR/AND on key=value pairs), **multi-value indexes on JSON arrays are the optimal choice** for direct tag queries, with generated columns providing backup for complex filtering.

Karate JSON reports include tags at both feature and scenario levels with structure `{"name": "@tagname", "line": N}`. Tags can be simple (`@smoke`) or key-value pairs (`@env=staging`). The test_result table's `test_suites_json` column can store these tags with proper JSON path extraction.

**Primary recommendation:** Use multi-value indexes on JSON tag arrays for the three core query patterns. Benchmark at 10K-100K scale to validate performance vs. junction table normalization. For expression parsing (`smoke AND (env=staging OR env=prod)`), use ANTLR4 or hand-rolled recursive descent parser.

## Standard Stack

The established libraries/tools for this domain:

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| MySQL | 8.0.33+ | Database with JSON support | Production environment version, multi-value indexes require 8.0.17+ |
| JOOQ | 3.x | Type-safe SQL generation | Already in use, supports JSON functions via `field()` |
| Jackson | 2.x | JSON parsing/generation | Spring Boot default, handles Karate JSON |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| ANTLR4 | 4.x | Expression parser generator | Complex tag query grammar with precedence/grouping |
| Spring Data JPA | 2.6.x | Query abstraction | If adding @Query methods for tag searches |
| Testcontainers | 1.x | Integration testing | Benchmark different indexing strategies |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Multi-value index | Junction table | Junction table: better for extremely high tag cardinality (>50 tags/test), more storage, more complex joins. Multi-value: simpler schema, faster single-table queries. |
| ANTLR4 | Hand-rolled parser | Hand-rolled: less dependency weight, sufficient for simple grammar. ANTLR4: handles complex precedence, generates visitor pattern. |
| Generated columns | Functional indexes | Functional indexes: less storage (virtual). Generated columns: explicit in schema, easier debugging. |

**Installation:**
```bash
# Multi-value indexes require MySQL 8.0.17+
mysql --version  # Verify >= 8.0.17

# If adding ANTLR4 for expression parsing
# Add to reportcard-server/build.gradle:
implementation 'org.antlr:antlr4-runtime:4.13.1'
```

## Architecture Patterns

### Recommended Project Structure
```
reportcard-server/src/main/
├── java/.../persist/
│   ├── TagQueryBuilder.java          # Converts parsed expressions to JOOQ
│   └── TagSearchRepository.java      # Tag-specific queries
├── java/.../controller/
│   └── TagSearchController.java      # Dedicated tag search endpoint
└── resources/db/migration/
    └── V1.3__add_tag_indexes.sql     # Multi-value index DDL
```

### Pattern 1: Multi-Value Index on JSON Array
**What:** Index JSON array elements individually for efficient membership queries
**When to use:** Searching for tests matching specific tags (the primary use case)
**Example:**
```sql
-- Source: MySQL 8.0 Reference Manual
-- https://dev.mysql.com/doc/refman/8.0/en/create-index.html

-- Add tags column to test_suite table (feature-level tags)
ALTER TABLE test_suite
ADD COLUMN tags JSON DEFAULT NULL;

-- Create multi-value index on tag names (stripped @ prefix)
CREATE INDEX idx_test_suite_tags ON test_suite (
    (CAST(tags->'$[*].name' AS CHAR(100) ARRAY))
);

-- Query: Find suites tagged with 'smoke'
SELECT * FROM test_suite
WHERE 'smoke' MEMBER OF(JSON_EXTRACT(tags, '$[*].name'));

-- Query: Find suites with env=staging (key-value tag)
SELECT * FROM test_suite
WHERE 'env=staging' MEMBER OF(JSON_EXTRACT(tags, '$[*].name'));
```

### Pattern 2: Generated Column for Frequently Filtered Tag
**What:** Extract specific tag value into indexed generated column
**When to use:** When one tag dominates filtering (e.g., environment tag on 80% of queries)
**Example:**
```sql
-- Source: MySQL 8.0 Reference Manual
-- https://dev.mysql.com/doc/refman/8.0/en/create-table-generated-columns.html

-- Add generated column extracting 'env' tag value
ALTER TABLE test_suite
ADD COLUMN env_tag VARCHAR(50)
GENERATED ALWAYS AS (
    JSON_UNQUOTE(JSON_EXTRACT(tags,
        CONCAT('$[',
            JSON_SEARCH(tags, 'one', 'env=%', NULL, '$[*].name'),
            '].name'
        )
    ))
) VIRTUAL;

-- Index the generated column
CREATE INDEX idx_test_suite_env_tag ON test_suite(env_tag);

-- Query optimization: MySQL recognizes matching expression
SELECT * FROM test_suite WHERE env_tag = 'env=staging';
```

### Pattern 3: JOOQ Integration with JSON Functions
**What:** Use JOOQ's `field()` for JSON functions not in generated classes
**When to use:** Dynamic queries with tag filtering
**Example:**
```java
// Source: JOOQ documentation patterns
// https://www.jooq.org/doc/latest/manual/sql-building/column-expressions/

import static org.jooq.impl.DSL.*;

public class TagQueryBuilder {
    private final DSLContext dsl;

    // OR query: Match any of multiple tags
    public Condition tagMatchesAny(List<String> tags) {
        // Uses multi-value index via MEMBER OF
        Condition[] conditions = tags.stream()
            .map(tag -> condition(
                "{0} MEMBER OF(JSON_EXTRACT({1}, '$.{2}[*].name'))",
                val(tag),
                TEST_SUITE.TAGS,
                val("$")
            ))
            .toArray(Condition[]::new);
        return or(conditions);
    }

    // AND query: Match all specified tags
    public Condition tagMatchesAll(List<String> tags) {
        return and(tags.stream()
            .map(tag -> condition(
                "{0} MEMBER OF(JSON_EXTRACT({1}, '$.{2}[*].name'))",
                val(tag),
                TEST_SUITE.TAGS,
                val("$")
            ))
            .toArray(Condition[]::new)
        );
    }

    // Key-value filtering
    public Condition tagKeyValue(String key, List<String> values) {
        List<String> kvPairs = values.stream()
            .map(v -> key + "=" + v)
            .toList();
        return tagMatchesAny(kvPairs);
    }
}
```

### Pattern 4: Expression Parser with Recursive Descent
**What:** Parse tag expressions into AST for SQL generation
**When to use:** Supporting `smoke AND (env=staging OR env=prod)` syntax
**Example:**
```java
// Source: Standard recursive descent pattern
// Simplified expression grammar:
// expr     := term (OR term)*
// term     := factor (AND factor)*
// factor   := TAG | KEY=VALUE | ( expr )

public class TagExpressionParser {
    private final Tokenizer tokenizer;

    public TagExpr parse(String expression) {
        tokenizer = new Tokenizer(expression);
        return parseExpr();
    }

    private TagExpr parseExpr() {
        TagExpr left = parseTerm();
        while (tokenizer.match("OR")) {
            TagExpr right = parseTerm();
            left = new OrExpr(left, right);
        }
        return left;
    }

    private TagExpr parseTerm() {
        TagExpr left = parseFactor();
        while (tokenizer.match("AND")) {
            TagExpr right = parseFactor();
            left = new AndExpr(left, right);
        }
        return left;
    }

    private TagExpr parseFactor() {
        if (tokenizer.match("(")) {
            TagExpr expr = parseExpr();
            tokenizer.expect(")");
            return expr;
        }

        String token = tokenizer.next();
        if (token.contains("=")) {
            String[] parts = token.split("=", 2);
            return new KeyValueTag(parts[0], parts[1]);
        }
        return new SimpleTag(token);
    }
}

// Convert AST to JOOQ Condition
public interface TagExpr {
    Condition toCondition(TagQueryBuilder builder);
}

record SimpleTag(String name) implements TagExpr {
    public Condition toCondition(TagQueryBuilder builder) {
        return builder.tagMatchesAny(List.of(name));
    }
}

record OrExpr(TagExpr left, TagExpr right) implements TagExpr {
    public Condition toCondition(TagQueryBuilder builder) {
        return or(left.toCondition(builder), right.toCondition(builder));
    }
}
```

### Anti-Patterns to Avoid
- **JSON_SEARCH for filtering:** JSON_SEARCH has no index support, causes full table scans. Use MEMBER OF or JSON_CONTAINS with multi-value indexes instead.
- **Direct JSON column in WHERE:** `WHERE test_suites_json LIKE '%smoke%'` bypasses all indexes and scans entire JSON blobs. Always extract to indexed paths.
- **Mixing collations:** JSON string extraction defaults to utf8mb4_bin. Use explicit COLLATE or CAST to match index collation.
- **Empty arrays without NULL handling:** Empty JSON arrays create no index entries. Queries expecting tags will miss records with `[]` unless explicitly checked.

## Don't Hand-Roll

Problems that look simple but have existing solutions:

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Expression parser | Regex-based tag matcher | ANTLR4 or recursive descent | Proper precedence handling, extensibility for NOT/grouping, tested grammar |
| JSON path extraction | String manipulation on JSON | Jackson JsonPointer or MySQL JSON_EXTRACT | Handles escaping, nested paths, array indices correctly |
| SQL injection in dynamic queries | String concatenation | JOOQ bind variables with val() | Type-safe, parameterized queries prevent injection |
| Benchmark test data | Manual INSERT statements | Testcontainers + Faker library | Realistic distributions, repeatable seeds, volume generation |

**Key insight:** JSON indexing has subtle collation, type coercion, and NULL handling pitfalls. Multi-value indexes silently fail (fall back to full scan) when expressions don't match index definition exactly. Always verify with EXPLAIN.

## Common Pitfalls

### Pitfall 1: Multi-Value Index Limits
**What goes wrong:** Queries fail with "undo log limit exceeded" or index not used
**Why it happens:** MySQL multi-value indexes limited to 65,221 bytes per record (~1,600 integer keys observed). Empty arrays create no index entries.
**How to avoid:**
- Monitor max tags per test (keep under 200 string tags ~50 chars each)
- Add application validation rejecting tests with excessive tags
- Use junction table fallback if tag counts regularly exceed limits
**Warning signs:**
- EXPLAIN shows table scan instead of index scan
- Error 1206 "undo log size exceeded" on INSERT/UPDATE
- Performance degrades only on high-tag-count tests

### Pitfall 2: Collation Mismatch Prevents Index Usage
**What goes wrong:** Index exists but queries do full table scan
**Why it happens:** JSON strings extract as utf8mb4_bin but query uses utf8mb4_0900_ai_ci (case-insensitive). MySQL can't match different collations.
**How to avoid:**
```sql
-- Option 1: Match index collation in queries
CREATE INDEX idx_tags ON test_suite (
    (CAST(tags->'$[*].name' AS CHAR(100) COLLATE utf8mb4_bin ARRAY))
);
SELECT * FROM test_suite
WHERE 'Smoke' COLLATE utf8mb4_bin MEMBER OF(...);  -- Case-sensitive

-- Option 2: Use case-insensitive collation in index
CREATE INDEX idx_tags ON test_suite (
    (CAST(tags->'$[*].name' AS CHAR(100) COLLATE utf8mb4_0900_ai_ci ARRAY))
);
SELECT * FROM test_suite
WHERE 'smoke' MEMBER OF(...);  -- Case-insensitive, matches 'Smoke', 'SMOKE'
```
**Warning signs:**
- EXPLAIN shows "Using where" without "Using index"
- Same query fast on small tables, slow on large tables
- Case-insensitive search returns no results despite visible matches

### Pitfall 3: Storing Tags at Wrong Hierarchy Level
**What goes wrong:** Feature tags stored only in test_suite, but queries need scenario-level filtering
**Why it happens:** Karate JSON has tags at both feature and scenario level. Storing only feature-level loses granularity.
**How to avoid:**
- Store feature tags in `test_suite.tags`
- Store scenario tags in `test_case.tags` (requires schema addition)
- For traceability queries, filter at appropriate level (test_result → test_suite → test_case)
**Warning signs:**
- Users ask "why can't I filter by scenario tag X?"
- Tag counts don't match Karate JSON input
- Duplicate results when joining for scenario tags

### Pitfall 4: Expression Parser Operator Precedence
**What goes wrong:** Query `smoke AND env=staging OR env=prod` parsed as `smoke AND (env=staging OR env=prod)` instead of `(smoke AND env=staging) OR env=prod`
**Why it happens:** Natural language precedence differs from standard boolean algebra (AND binds tighter than OR)
**How to avoid:**
- Document precedence clearly in API docs: "AND has higher precedence than OR. Use parentheses for clarity."
- Enforce parentheses in grammar: reject ambiguous expressions
- Follow standard operator precedence: NOT > AND > OR
**Warning signs:**
- User reports "query returns wrong results"
- Results change unexpectedly when adding more clauses
- Test coverage gaps in parser unit tests

## Code Examples

Verified patterns from official sources:

### Multi-Value Index Creation
```sql
-- Source: MySQL 8.0 Reference Manual
-- https://dev.mysql.com/doc/refman/8.0/en/create-index.html#create-index-multi-valued

-- Schema change V1.3
ALTER TABLE test_suite
ADD COLUMN tags JSON DEFAULT NULL COMMENT 'Feature-level tags from Karate JSON';

ALTER TABLE test_case
ADD COLUMN tags JSON DEFAULT NULL COMMENT 'Scenario-level tags from Karate JSON';

-- Multi-value indexes for tag search
CREATE INDEX idx_test_suite_tags ON test_suite (
    (CAST(tags->'$[*].name' AS CHAR(100) COLLATE utf8mb4_0900_ai_ci ARRAY))
) COMMENT 'Enables MEMBER OF queries on tag names (case-insensitive)';

CREATE INDEX idx_test_case_tags ON test_case (
    (CAST(tags->'$[*].name' AS CHAR(100) COLLATE utf8mb4_0900_ai_ci ARRAY))
) COMMENT 'Enables MEMBER OF queries on scenario tags';
```

### JSON Extraction from Karate Format
```java
// Source: Jackson JsonNode patterns
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class KarateTagExtractor {

    /**
     * Extract tags from Karate JSON, stripping @ prefix
     * Input:  [{"name": "@smoke", "line": 3}, {"name": "@env=staging", "line": 3}]
     * Output: ["smoke", "env=staging"]
     */
    public List<String> extractTags(JsonNode tagsArray) {
        if (tagsArray == null || !tagsArray.isArray()) {
            return List.of();
        }

        List<String> tags = new ArrayList<>();
        for (JsonNode tagObj : tagsArray) {
            String name = tagObj.path("name").asText();
            // Strip @ prefix for storage
            if (name.startsWith("@")) {
                name = name.substring(1);
            }
            tags.add(name);
        }
        return tags;
    }

    /**
     * Convert extracted tags back to Karate format with @ prefix
     * Used for display in UI
     */
    public ArrayNode formatForDisplay(List<String> tags, JsonNodeFactory factory) {
        ArrayNode array = factory.arrayNode();
        tags.forEach(tag -> {
            ObjectNode obj = factory.objectNode();
            obj.put("name", "@" + tag);
            obj.put("line", 0);  // Line number not preserved in storage
            array.add(obj);
        });
        return array;
    }
}
```

### Tag Query Endpoint Design
```java
// Source: Spring REST best practices
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
public class TagSearchController {

    private final TagSearchService tagSearchService;

    /**
     * Simple tag filtering via query params on existing browse endpoint
     * GET /api/v1/test-results?tags=smoke,regression&tagMatch=any
     */
    @GetMapping("/test-results")
    public Page<TestResultSummary> searchByTags(
        @RequestParam List<String> tags,
        @RequestParam(defaultValue = "any") String tagMatch,  // "any" or "all"
        Pageable pageable
    ) {
        if ("all".equalsIgnoreCase(tagMatch)) {
            return tagSearchService.findMatchingAllTags(tags, pageable);
        } else {
            return tagSearchService.findMatchingAnyTag(tags, pageable);
        }
    }

    /**
     * Advanced tag search with expression syntax
     * POST /api/v1/tags/search
     * Body: {"expression": "smoke AND (env=staging OR env=prod)"}
     */
    @PostMapping("/search")
    public Page<TestResultSummary> searchByExpression(
        @RequestBody TagSearchRequest request,
        Pageable pageable
    ) {
        TagExpr expr = tagExpressionParser.parse(request.expression());
        return tagSearchService.findByExpression(expr, pageable);
    }

    /**
     * Functional traceability: latest run with data per job by tag
     * GET /api/v1/tags/latest?job=myJob&tags=smoke&hasData=true
     */
    @GetMapping("/latest")
    public Map<String, TestResultSummary> latestByJobAndTag(
        @RequestParam String job,
        @RequestParam List<String> tags,
        @RequestParam(defaultValue = "true") boolean hasData
    ) {
        return tagSearchService.findLatestByJobAndTags(job, tags, hasData);
    }
}

record TagSearchRequest(String expression) {}
```

### Benchmark Test Harness
```java
// Source: JUnit 5 + Testcontainers pattern
import org.testcontainers.containers.MySQLContainer;
import org.junit.jupiter.params.ParameterizedTest;

@Testcontainers
class TagIndexBenchmarkTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33");

    @ParameterizedTest
    @CsvSource({
        "multi_value_index, 10000, 10",
        "multi_value_index, 100000, 10",
        "generated_column, 10000, 10",
        "junction_table, 10000, 10"
    })
    void benchmarkIndexStrategy(String strategy, int recordCount, int tagsPerRecord) {
        // Setup: Create schema with specified index strategy
        setupSchema(strategy);

        // Load: Insert test data
        List<TestSuite> data = generateTestData(recordCount, tagsPerRecord);
        bulkInsert(data);

        // Query 1: OR list on tag keys
        long start = System.nanoTime();
        List<TestSuite> results = queryTagMatchesAny(List.of("smoke", "regression", "critical"));
        long orQueryNanos = System.nanoTime() - start;

        // Query 2: AND list on key=value pairs
        start = System.nanoTime();
        results = queryTagMatchesAll(List.of("env=staging", "browser=chrome"));
        long andQueryNanos = System.nanoTime() - start;

        // Query 3: Complex expression
        start = System.nanoTime();
        results = queryExpression("smoke AND (env=staging OR env=prod)");
        long exprQueryNanos = System.nanoTime() - start;

        // Report
        System.out.printf("%s,%d,%d,%d,%d,%d%n",
            strategy, recordCount, tagsPerRecord,
            orQueryNanos / 1_000_000,  // ms
            andQueryNanos / 1_000_000,
            exprQueryNanos / 1_000_000
        );
    }
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Full-text indexes on JSON | Multi-value indexes | MySQL 8.0.17 (2019) | 10-100x faster array membership queries, proper type handling |
| Generated STORED columns | Generated VIRTUAL columns | MySQL 5.7+ | Virtual columns save storage, no write overhead, sufficient for indexes |
| Hand-coded parsers | ANTLR4 code generation | ANTLR4 matured 2013+ | Visitor pattern, tested grammar, easier maintenance |
| String LIKE queries | JSON_CONTAINS/MEMBER OF | MySQL 5.7 JSON functions | Type-safe comparisons, index support, proper escaping |

**Deprecated/outdated:**
- **Full-text indexes on JSON:** MySQL doesn't support FTS directly on JSON. Workaround with generated columns possible but multi-value indexes are superior for tag search.
- **JSON_SEARCH in WHERE clauses:** No index support. Use extracted paths with MEMBER OF instead.
- **ALGORITHM=INPLACE for multi-value indexes:** Not supported; always uses ALGORITHM=COPY (table rebuild). Plan maintenance windows accordingly.

## Open Questions

Things that couldn't be fully resolved:

1. **Tag cardinality at scale**
   - What we know: Multi-value indexes limit 65,221 bytes per record
   - What's unclear: Actual production tag distribution in Karate tests (max tags per suite/scenario)
   - Recommendation: Log tag counts during Phase 3 Karate parsing. If P99 > 100 tags/test, consider junction table instead.

2. **Case sensitivity for tag matching**
   - What we know: Can choose utf8mb4_bin (case-sensitive) or utf8mb4_0900_ai_ci (case-insensitive) in index
   - What's unclear: User expectation — should `@Smoke` match query for `smoke`?
   - Recommendation: Default to case-insensitive (ai_ci) for better UX. Document behavior in API specs.

3. **Tag inheritance across hierarchy**
   - What we know: Karate has feature tags and scenario tags
   - What's unclear: Should scenario-level queries also return results matching only feature tags? (implicit inheritance)
   - Recommendation: No implicit inheritance — explicit is better. Query API can provide "tags_inherited" parameter if needed.

4. **Expression syntax extensibility**
   - What we know: Initial requirement is `AND`, `OR`, parentheses
   - What's unclear: Future need for `NOT`, regex wildcards, fuzzy matching
   - Recommendation: Design parser/AST to support `NOT` from day 1 (even if not exposed in API). Regex/fuzzy is separate feature, don't block on it.

## Sources

### Primary (HIGH confidence)
- MySQL 8.0 Reference Manual - JSON Functions: https://dev.mysql.com/doc/refman/8.0/en/json.html
- MySQL 8.0 Reference Manual - CREATE INDEX: https://dev.mysql.com/doc/refman/8.0/en/create-index.html
- MySQL 8.0 Reference Manual - JSON Search Functions: https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html
- Project schema: reportcard-server/src/main/resources/db/migration/V1.0__reportcard_mysql_ddl.sql
- Karate JSON sample: cucumber-json/karate-reports/delorean-create-dss-test.json

### Secondary (MEDIUM confidence)
- ANTLR4 documentation: https://www.antlr.org/ (standard for expression parsing)
- JOOQ documentation: https://www.jooq.org/doc/latest/manual/ (JSON function integration patterns)
- Jackson JsonNode API: https://fasterxml.github.io/jackson-databind/javadoc/2.13/

### Tertiary (LOW confidence)
- None used — all findings verified with official documentation or project source code

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - MySQL 8.0.33 confirmed, JOOQ/Jackson already in use, versions verified in gradle.properties
- Architecture: HIGH - All patterns from official MySQL docs with syntax verified, JOOQ integration standard
- Pitfalls: MEDIUM - Multi-value index limits documented, collation issues verified, tag hierarchy and expression precedence are logical concerns based on standard patterns but not project-specific tested

**Research date:** 2026-02-09
**Valid until:** 2026-03-09 (30 days — MySQL JSON indexing is stable, unlikely to change)
