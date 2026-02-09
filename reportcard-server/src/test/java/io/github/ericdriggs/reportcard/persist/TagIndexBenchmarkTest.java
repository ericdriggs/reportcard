package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.jooq.impl.DSL.*;

/**
 * Benchmark test to capture EXPLAIN output for multi-value index queries.
 * This is a research tool - run once to gather data, then document results.
 *
 * The test creates a temporary table, adds multi-value index, loads 10K rows,
 * and runs EXPLAIN FORMAT=JSON for three query patterns.
 */
@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class TagIndexBenchmarkTest {

    @Autowired
    private DSLContext dsl;

    private static final int ROW_COUNT = 1000;
    private static final String[] SIMPLE_TAGS = {"smoke", "regression", "critical", "integration", "unit", "slow", "fast", "flaky"};
    private static final String[] KEY_VALUE_TAGS = {"env=staging", "env=prod", "env=dev", "browser=chrome", "browser=firefox", "browser=safari", "priority=high", "priority=low"};

    @Test
    void benchmarkMultiValueIndex() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TAG INDEX BENCHMARK - Multi-Value Index on JSON Array");
        System.out.println("=".repeat(80) + "\n");

        // 1. Create test table
        createTestTable();

        // 2. Create multi-value index
        createMultiValueIndex();

        // 3. Load test data
        loadTestData();

        // 4. Run EXPLAIN for each query pattern
        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 1a: OR with single WHERE (won't use index)");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark
            WHERE 'smoke' MEMBER OF(tags)
               OR 'regression' MEMBER OF(tags)
               OR 'critical' MEMBER OF(tags)
            """);

        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 1b: OR via UNION (each leg uses index)");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark WHERE 'smoke' MEMBER OF(tags)
            UNION
            SELECT * FROM tag_benchmark WHERE 'regression' MEMBER OF(tags)
            UNION
            SELECT * FROM tag_benchmark WHERE 'critical' MEMBER OF(tags)
            """);

        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 2: OR list on key=value pairs (env=staging OR env=prod)");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark
            WHERE 'env=staging' MEMBER OF(tags)
               OR 'env=prod' MEMBER OF(tags)
            """);

        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 3a: AND with single WHERE");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark
            WHERE 'env=staging' MEMBER OF(tags)
              AND 'browser=chrome' MEMBER OF(tags)
            """);

        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 3b: AND via INTERSECT (each leg uses index)");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark WHERE 'env=staging' MEMBER OF(tags)
            INTERSECT
            SELECT * FROM tag_benchmark WHERE 'browser=chrome' MEMBER OF(tags)
            """);

        // 5. Test JSON_OVERLAPS (documented to work better with multi-value index)
        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 4: JSON_OVERLAPS on raw column");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark
            WHERE JSON_OVERLAPS(tags, '["smoke", "regression", "critical"]')
            """);

        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 5a: JSON_OVERLAPS with $[*] path (matches idx_tags_path)");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark
            WHERE JSON_OVERLAPS(tags, CAST('["smoke", "regression", "critical"]' AS JSON))
            """);

        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 5b: JSON_OVERLAPS on tags directly (matches idx_tags_direct)");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark
            WHERE JSON_OVERLAPS(tags, CAST('["smoke", "regression", "critical"]' AS JSON))
            """);

        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 5c: MEMBER OF on tags directly");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark
            WHERE 'smoke' MEMBER OF(tags)
            """);

        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 6: Single MEMBER OF (baseline)");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark
            WHERE 'smoke' MEMBER OF(tags)
            """);

        System.out.println("\n" + "-".repeat(80));
        System.out.println("QUERY PATTERN 7: JSON_CONTAINS with CAST (AND semantics)");
        System.out.println("-".repeat(80));
        runExplainQuery("""
            SELECT * FROM tag_benchmark
            WHERE JSON_CONTAINS(tags, CAST('["smoke", "regression"]' AS JSON))
            """);

        // 6. Also test actual query execution times
        System.out.println("\n" + "-".repeat(80));
        System.out.println("ACTUAL QUERY EXECUTION");
        System.out.println("-".repeat(80));
        runTimedQuery("OR on tag keys", """
            SELECT COUNT(*) FROM tag_benchmark
            WHERE 'smoke' MEMBER OF(tags)
               OR 'regression' MEMBER OF(tags)
               OR 'critical' MEMBER OF(tags)
            """);
        runTimedQuery("OR on key=value", """
            SELECT COUNT(*) FROM tag_benchmark
            WHERE 'env=staging' MEMBER OF(tags)
               OR 'env=prod' MEMBER OF(tags)
            """);
        runTimedQuery("AND on key=value", """
            SELECT COUNT(*) FROM tag_benchmark
            WHERE 'env=staging' MEMBER OF(tags)
              AND 'browser=chrome' MEMBER OF(tags)
            """);

        // Cleanup
        dsl.execute("DROP TABLE IF EXISTS tag_benchmark");

        System.out.println("\n" + "=".repeat(80));
        System.out.println("BENCHMARK COMPLETE");
        System.out.println("=".repeat(80) + "\n");
    }

    private void createTestTable() {
        System.out.println("Creating test table...");

        // Set connection collation to match what multi-value index requires
        dsl.execute("SET NAMES utf8mb4 COLLATE utf8mb4_0900_as_cs");
        System.out.println("Connection collation set to utf8mb4_0900_as_cs");

        dsl.execute("DROP TABLE IF EXISTS tag_benchmark");
        // Use utf8mb4_0900_as_cs collation per MySQL multi-value index requirements
        dsl.execute("""
            CREATE TABLE tag_benchmark (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255),
                tags JSON DEFAULT NULL
            ) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs
            """);
        System.out.println("Test table created (utf8mb4_0900_as_cs).");
    }

    private void createMultiValueIndex() {
        System.out.println("Creating multi-value index...");

        // Simple syntax - table collation set to utf8mb4_0900_as_cs
        try {
            dsl.execute("""
                CREATE INDEX idx_tags ON tag_benchmark (
                    (CAST(tags AS CHAR(100) ARRAY))
                )
                """);
            System.out.println("Index idx_tags created");
        } catch (Exception e) {
            System.out.println("ERROR creating idx_tags: " + e.getMessage());
            e.printStackTrace();
        }

        // Show MySQL version and index details
        try {
            var result = dsl.fetch("SELECT VERSION() as ver");
            System.out.println("MySQL version: " + result.get(0).get("ver"));

            // Show index definition including collation
            var indexInfo = dsl.fetch("SHOW CREATE TABLE tag_benchmark");
            System.out.println("Table definition:\n" + indexInfo.get(0).get(1));

            // Show default collation
            var collation = dsl.fetch("SELECT @@collation_database as db_collation, @@collation_connection as conn_collation");
            System.out.println("DB collation: " + collation.get(0).get("db_collation"));
            System.out.println("Connection collation: " + collation.get(0).get("conn_collation"));
        } catch (Exception e) {
            System.out.println("Could not get MySQL info: " + e.getMessage());
        }
    }

    private void loadTestData() {
        System.out.println("Loading " + ROW_COUNT + " rows with random tags...");
        Random random = new Random(42); // Fixed seed for reproducibility

        List<String> insertValues = new ArrayList<>();
        for (int i = 0; i < ROW_COUNT; i++) {
            List<String> tags = generateRandomTags(random);
            String tagsJson = "[" + String.join(",", tags.stream().map(t -> "\"" + t + "\"").toList()) + "]";
            insertValues.add(String.format("('test_%d', '%s')", i, tagsJson));

            // Batch insert every 1000 rows
            if (insertValues.size() >= 1000) {
                dsl.execute("INSERT INTO tag_benchmark (name, tags) VALUES " + String.join(",", insertValues));
                insertValues.clear();
            }
        }
        // Insert remaining
        if (!insertValues.isEmpty()) {
            dsl.execute("INSERT INTO tag_benchmark (name, tags) VALUES " + String.join(",", insertValues));
        }

        // Verify row count
        Result<Record> result = dsl.fetch("SELECT COUNT(*) as cnt FROM tag_benchmark");
        System.out.println("Loaded rows: " + result.get(0).get("cnt"));

        // Update optimizer statistics
        System.out.println("Running ANALYZE TABLE to update statistics...");
        dsl.execute("ANALYZE TABLE tag_benchmark");
        System.out.println("Statistics updated.");

        // Show tag distribution
        Result<Record> tagStats = dsl.fetch("""
            SELECT
                MIN(JSON_LENGTH(tags)) as min_tags,
                MAX(JSON_LENGTH(tags)) as max_tags,
                AVG(JSON_LENGTH(tags)) as avg_tags
            FROM tag_benchmark
            """);
        System.out.println("Tag distribution - min: " + tagStats.get(0).get("min_tags") +
                          ", max: " + tagStats.get(0).get("max_tags") +
                          ", avg: " + tagStats.get(0).get("avg_tags"));
    }

    private List<String> generateRandomTags(Random random) {
        List<String> tags = new ArrayList<>();
        int tagCount = 5 + random.nextInt(11); // 5-15 tags

        // Add some simple tags
        for (int i = 0; i < tagCount / 2; i++) {
            tags.add(SIMPLE_TAGS[random.nextInt(SIMPLE_TAGS.length)]);
        }
        // Add some key-value tags
        for (int i = 0; i < tagCount / 2; i++) {
            tags.add(KEY_VALUE_TAGS[random.nextInt(KEY_VALUE_TAGS.length)]);
        }

        return tags.stream().distinct().toList(); // Remove duplicates
    }

    private void runExplainQuery(String query) {
        try {
            Result<Record> result = dsl.fetch("EXPLAIN FORMAT=JSON " + query);
            String explainJson = result.get(0).get(0, String.class);
            System.out.println("\nEXPLAIN FORMAT=JSON output:");
            System.out.println(explainJson);

            // Also run regular EXPLAIN for quick summary
            Result<Record> regularExplain = dsl.fetch("EXPLAIN " + query);
            System.out.println("\nEXPLAIN summary:");
            for (Record record : regularExplain) {
                System.out.println("  type: " + record.get("type") +
                                  ", possible_keys: " + record.get("possible_keys") +
                                  ", key: " + record.get("key") +
                                  ", rows: " + record.get("rows") +
                                  ", Extra: " + record.get("Extra"));
            }
        } catch (Exception e) {
            System.out.println("ERROR running EXPLAIN: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runTimedQuery(String name, String query) {
        try {
            long start = System.nanoTime();
            Result<Record> result = dsl.fetch(query);
            long elapsed = System.nanoTime() - start;
            System.out.println(name + ": " + result.get(0).get(0) + " rows, " + (elapsed / 1_000_000) + "ms");
        } catch (Exception e) {
            System.out.println(name + " ERROR: " + e.getMessage());
        }
    }
}
