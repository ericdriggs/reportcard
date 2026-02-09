# Testing Patterns

**Analysis Date:** 2026-01-26

## Test Framework

**Runner:**
- JUnit Jupiter (JUnit 5) via `junit-jupiter-api` and `junit-jupiter-engine`
- Gradle test runner configured in `build.gradle.kts` with `useJUnitPlatform()`
- Config: `build.gradle.kts` (line 58-70), `build.gradle` (reportcard-server)

**Assertion Library:**
- JUnit assertions: `org.junit.jupiter.api.Assertions.*`
- Hamcrest matchers: `org.hamcrest.Matchers.*` via `import static org.hamcrest.MatcherAssert.assertThat`
- JSON assertions: `net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals()` for JSON comparison

**Run Commands:**
```bash
./gradlew test                      # Run all unit tests
./gradlew integrationTest           # Run integration tests in integrationTest source set
./gradlew test --tests "fully.qualified.TestClassName"  # Run single test class
```

**Coverage:**
- JaCoCo configured in `build.gradle.kts` (lines 41-79)
- Exclusions: `io/github/ericdriggs/reportcard/gen/**` (generated JOOQ code excluded)
- Reports: XML and HTML generated to `build/reports/jacoco/`
- View coverage: `./gradlew jacocoTestReport` generates HTML report

## Test File Organization

**Location:**
- Co-located with source: `src/test/java/` mirrors `src/main/java/` package structure
- Integration tests: separate source set `src/integrationTest/java/`
- Test resources: `src/test/resources/` (e.g., test data, properties files)

**Naming:**
- Unit tests: `*Test.java` suffix (e.g., `StageDetailsTest.java`, `NumberStringUtilTest.java`)
- Integration tests: `*IntegrationTest.java` suffix (e.g., `JobDashboardEndpointsIntegrationTest.java`)
- Base classes: `Abstract*Test.java` (e.g., `AbstractTestResultPersistTest.java`)

**Structure:**
```
src/
├── test/java/
│   ├── io/github/ericdriggs/reportcard/
│   │   ├── util/        # Unit tests for utilities
│   │   ├── model/       # Unit tests for models
│   │   ├── controller/  # Integration tests for controllers
│   │   ├── persist/     # Integration tests for persistence layer
│   │   ├── config/      # Test configuration (TestConfig.java, MyEmbeddedMysql.java)
│   │   └── gen/db/      # Tests for generated JOOQ code
│   └── jooq/            # JOOQ-specific tests
├── integrationTest/java/
│   └── io/github/ericdriggs/reportcard/
│       ├── controller/  # Full integration tests with containers
│       └── persist/     # Persistence integration tests
└── test/resources/
    ├── application-test.properties
    ├── format-samples/  # Test XML files (junit, surefire, testng)
    └── db/migration/    # Schema scripts for Testcontainers
```

## Test Structure

**Suite Organization:**
```java
@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class JunitControllerTest {

    @Autowired
    private JunitController junitController;

    @Autowired
    private S3Service s3Service;

    @Test
    void postJunitTest() throws IOException {
        // Arrange
        final String stage = "postJunitTest";
        final String xmlClassPath = "format-samples/sample-junit-small.xml";

        // Act
        StagePathTestResult result = postJunitFixture(stage, xmlClassPath);

        // Assert
        assertNotNull(result);
        assertEquals(stage, result.getStagePath().getStage().getStageName());
    }
}
```

**Patterns:**
- Arrange-Act-Assert (AAA) pattern followed
- Setup via Spring `@Autowired` fields (DI for services under test)
- Test data created via builder pattern (e.g., `StageDetails.builder()`)
- Assertions use JUnit Jupiter and Hamcrest matchers

**Base Test Classes:**
- `AbstractTestResultPersistTest.java`: Base for persistence tests with `TestResultPersistService` autowired
- All integration tests extend Spring `@SpringBootTest` with Testcontainers
- Constructor injection used for test service dependencies (example: `AbstractTestResultPersistTest`)

## Test Infrastructure

**Testcontainers:**
- MySQL 8.0.33: Defined in `src/test/java/io/github/ericdriggs/reportcard/config/MyEmbeddedMysql.java`
- LocalStack for S3: Defined in `src/test/java/io/github/ericdriggs/reportcard/config/LocalStackConfig.java`
- Containers initialized per test class (not per method)
- Schema auto-initialized: SQL files from `src/main/resources/db/migration/` copied to container's `/docker-entrypoint-initdb.d/`

**Configuration:**
- `@ActiveProfiles("test")`: Activates Spring test profile
- `@TestPropertySource(locations = "classpath:application-test.properties")`: Overrides production properties
- Properties file at `src/test/resources/application-test.properties`: Contains test DB/S3 URLs

**Test Data:**
- `TestData.java` (in generated JOOQ package): Constants for company, org, repo, branch, sha
- XML test files: `src/test/resources/format-samples/` (JUnit, Surefire, TestNG samples)
- Builder pattern for complex objects (e.g., `StageDetails.builder().company(...).build()`)

## Mocking

**Framework:**
- Mockito not explicitly configured; tests use real Spring beans with Testcontainers
- Alternative: Mock via Spring `@MockBean` when needed (not observed in current tests)
- Spy pattern: Not widely used

**Patterns:**
```java
// Integration test with real database
@SpringBootTest(classes = ReportcardApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JunitControllerTest {
    @Autowired
    private JunitController junitController;  // Real bean

    @Test
    void postJunitTest() {
        // Uses real JunitController, S3Service, TestResultPersistService
        StagePathTestResult result = junitController.postJunitXml(...);
    }
}
```

**What to Mock:**
- External APIs (typically): Not mocked; LocalStack provides S3 mock
- Services: Autowired directly; use Spring profiles to swap implementations
- Database: Testcontainers provides real MySQL; don't mock

**What NOT to Mock:**
- Domain model classes (TestResultModel, StageDetails)
- Services under test (should be real beans)
- Database layer (use Testcontainers instead)

## Fixtures and Factories

**Test Data:**
```java
// Builder-based fixture creation
static StageDetails getStageDetails(String stageName) {
    return StageDetails.builder()
            .company(TestData.company)
            .org(TestData.org)
            .repo(TestData.repo)
            .branch(TestData.branch)
            .sha(TestData.sha)
            .jobInfo(TestData.jobInfo)
            .runReference(TestData.runReference)
            .stage(stageName)
            .build();
}

// Variant with custom runReference
static StageDetails getStageDetails(String stageName, UUID runReference) {
    return StageDetails.builder()
            // ... same as above ...
            .runReference(runReference)
            .build();
}
```

**Location:**
- Test methods define helpers as `static` methods within test class
- Shared test constants in `TestData.java` (generated JOOQ package)
- XML fixtures in `src/test/resources/format-samples/`

**Builder Usage:**
- Preferred pattern for all domain objects
- TreeMap used for ordered collections (e.g., `jobInfo`)
- Immutable records require full builder setup (no setters)

## Coverage

**Requirements:** None explicitly enforced; JaCoCo runs but no minimum threshold set

**View Coverage:**
```bash
./gradlew test jacocoTestReport
# Report at: build/reports/jacoco/test/html/index.html
```

**Exclusions:**
- JOOQ generated code excluded from coverage (line 66-68 in build.gradle.kts)
- Rationale: Generated code not hand-written; coverage metrics less meaningful

## Test Types

**Unit Tests:**
- Scope: Individual utilities and model classes
- Approach: No Spring context; pure Java unit tests
- Examples: `NumberStringUtilTest.java`, `StageDetailsTest.java`
- Dependencies: None (or minimal, via static helpers)

**Integration Tests:**
- Scope: Services with database and S3 interaction
- Approach: Full Spring context with Testcontainers
- Examples: `JunitControllerTest.java`, `StorageControllerTest.java`
- Dependencies: Real MySQL, real (mocked) S3 via LocalStack

**E2E Tests:**
- Scope: REST endpoints end-to-end
- Approach: `@SpringBootTest` with `webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT`
- Examples: `JobDashboardEndpointsIntegrationTest.java`
- Server runs on random port; tests invoke endpoints directly

## Common Patterns

**Async Testing:**
- Java 17 platform threads (no explicit async framework observed)
- Cache layer uses async updates (`AbstractAsyncCache.java`)
- Test pattern: Block on cache calls or use `Thread.sleep()` for verification (simple approach)

**Error Testing:**
```java
@Test
void testValidationError() {
    StageDetails.builder()
            // Missing required field
            .company(null)
            .org(TestData.org)
            // ... etc ...
            .build();  // Throws ResponseStatusException(HttpStatus.BAD_REQUEST)
}
```

**Response Verification:**
```java
@Test
void testJsonResponse() {
    ResponseEntity<String> response = graphUIController.getJobDashboard("hulu", "SubLife", null, 90);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    // Optional: parse and verify JSON structure
}
```

**Multi-Assertion Tests:**
```java
@Test
void jobInfoTest() {
    StageDetails stageDetails = StageDetails.builder()
            .company(TestData.company)
            // ... build ...
            .build();

    assertEquals("{\"application\":\"application1\",\"host\":\"host1\",\"pipeline\":\"pipeline1\"}",
                 stageDetails.getJobInfoJson());
}
```

## Test Execution Context

**Profiles:**
- `test` profile: Spring loads `application-test.properties`
- Properties override production values (DB URL, S3 endpoint, etc.)

**Ordering:**
- Unit tests run first: `./gradlew test`
- Integration tests run after: `./gradlew integrationTest` (should run after tests)
- Gradle configuration (line 289): `shouldRunAfter test`

**Isolation:**
- Each test class gets fresh Testcontainers instances
- Database state cleared between classes (containers fresh start)
- No shared state between test classes

---

*Testing analysis: 2026-01-26*
