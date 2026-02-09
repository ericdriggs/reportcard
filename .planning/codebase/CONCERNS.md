# Codebase Concerns

**Analysis Date:** 2026-01-26

## Tech Debt

**Plain-text credentials in properties files:**
- Issue: Service and database credentials (username/password) are hardcoded in application.properties files with TODOs indicating intent to secure them. Currently `service.username=test` and `service.password=test` in production properties.
- Files:
  - `reportcard-server/src/main/resources/application.properties` (lines 1-3, 26-27)
  - `reportcard-server/src/integrationTest/resources/application-integration-test.properties` (lines 1-3, 27)
  - `reportcard-server/src/test/resources/application-test.properties` (lines 1-3, 21)
- Impact: Credentials exposed in version control, insufficient authentication model, violates security best practices
- Fix approach: Migrate to environment-variable-only configuration, implement secure credential storage (Vault, AWS Secrets Manager), remove test credentials from production files

**Thread management without ExecutorService:**
- Issue: `AbstractAsyncCache` spawns raw threads with `new Thread()` and `.start()` instead of using thread pools or ExecutorService
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/cache/AbstractAsyncCache.java` (lines 107-108)
- Impact: Uncontrolled thread creation can lead to resource exhaustion, poor thread naming/monitoring, no graceful shutdown
- Fix approach: Replace with ScheduledExecutorService or CompletableFuture.supplyAsync(), implement proper lifecycle management

**Race condition in insert operations:**
- Issue: Code comment explicitly documents missing test for race condition on buildstage path insert where data could be missing from database
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StagePathPersistService.java` (line 210)
- Impact: Concurrent inserts could fail silently or produce inconsistent data; recovery mechanism is not tested
- Fix approach: Add test simulating concurrent inserts on same buildstage path; verify that insert failures are gracefully handled and existing data is returned

**Stale lastRun timestamp:**
- Issue: `TestResultPersistService` updates `lastRun` using `now()` instead of reading from job, creating potential for stale data
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/TestResultPersistService.java` (line 97)
- Impact: Job run tracking could show incorrect timestamps if operation is deferred
- Fix approach: Refactor to read lastRun from job entity instead of generating timestamp

**Synchronization using string concatenation:**
- Issue: `BrowseService` synchronizes on dynamically created strings (`job.getJobInfoStr() + ":" + run.getRunId()`), which creates new String instances each time
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/BrowseService.java` (line 608)
- Impact: Synchronization not effective; different threads could create different String objects and bypass lock contention
- Fix approach: Use a proper lock object or ConcurrentHashMap with proper synchronization strategy

**Incomplete/hardcoded filter logic:**
- Issue: BrowseService contains TODOs for incomplete filter implementation
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/BrowseService.java` (lines 252, 749)
- Impact: Jobs cannot be filtered properly; query logic requires refactoring
- Fix approach: Complete job filtering implementation; refactor filter parameter handling

**S3 resource lifecycle issues:**
- Issue: S3 clients created with `getS3Client()` and `getS3AsyncClient()` may not be properly closed
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/S3Service.java` (lines 65-75, 77-80)
- Impact: Resource leaks; connection pool exhaustion in long-running processes
- Fix approach: Implement try-with-resources or lifecycle management; consider singleton pattern with proper shutdown hooks

## Known Bugs

**Potential NPE in StagePathPersistService:**
- Symptoms: Null pointer exception when stage data is missing
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StagePathPersistService.java` (lines 217-224)
- Trigger: Calling `getOrInsertStage()` when stage path has null stage
- Workaround: Check for null stage before processing; covered by race condition TODO

**Inconsistent error handling in converters:**
- Symptoms: Generic RuntimeException wrapping of checked exceptions
- Files:
  - `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/TestResultModel.java` (line 138)
  - `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/xml/ResultCount.java` (line 114)
  - `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/TestResultPersistService.java` (line 109)
- Trigger: Any parsing/conversion error
- Workaround: Catch RuntimeException and log; difficult to handle specific errors

**printStackTrace() calls in production code:**
- Symptoms: Error traces printed to stderr instead of using logging framework
- Files:
  - `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/xml/ResultCount.java` (line 114)
  - `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/cache/model/StaticBrowseService.java` (line 34)
  - `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/merge/JunitFileMergerUtil.java` (line 32)
- Trigger: When exceptions occur in these code paths
- Workaround: Use proper logging configuration; difficult to aggregate and monitor

## Security Considerations

**Missing Authorization/Authentication:**
- Risk: No Spring Security implemented; basic placeholder auth with hardcoded username/password
- Files: `reportcard-server/src/main/resources/application.properties` (lines 26-27)
- Current mitigation: None - code comment indicates awareness but not implemented
- Recommendations:
  - Implement Spring Security with role-based access control
  - Use OAuth2 or JWT for API authentication
  - Add API key validation for client integrations
  - Audit who can access which companies/orgs/repos

**S3 credentials exposure:**
- Risk: AWS credentials obtained via `DefaultCredentialsProvider`; if running in unsafe environment, credentials could be exposed
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/S3Service.java` (line 67, 78)
- Current mitigation: Relies on AWS SDK credential chain
- Recommendations:
  - Enforce IAM role usage in production (EC2/ECS/Lambda)
  - Document credential requirement in deployment guide
  - Add encryption for S3 buckets

**Log masking for sensitive data:**
- Risk: Authorization headers and passwords could be logged
- Files:
  - `reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/PostWebClient.java` (line 31 - TODO)
  - `reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/LogFilters.java` (line 15 - TODO)
  - `reportcard-client/src/main/java/io/github/ericdriggs/reportcard/client/ClientApplication.java` (line 22 - TODO)
- Current mitigation: TODOs indicate awareness but not implemented
- Recommendations:
  - Implement sanitization filter for HTTP logs
  - Mask sensitive headers (Authorization, passwords)
  - Review all logging statements for credential leakage

**Uploaded file validation:**
- Risk: No apparent validation of uploaded XML files; arbitrary multipart uploads accepted
- Files: `reportcard-server/src/main/resources/application.properties` (lines 29-30) - file size limits only
- Current mitigation: File size limits (200MB max file, 300MB max request)
- Recommendations:
  - Validate XML structure before storage
  - Implement virus scanning for uploaded files
  - Enforce file type restrictions
  - Add rate limiting to prevent abuse

## Performance Bottlenecks

**Large service classes with complex queries:**
- Problem: `BrowseService` (753 lines), `GraphService` (686 lines) contain complex JOIN logic that could be optimized
- Files:
  - `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/BrowseService.java`
  - `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/GraphService.java`
- Cause: Multiple LEFT JOINs with recursive result mapping; no query optimization or pagination hints
- Improvement path:
  - Profile N+1 queries
  - Implement query result caching at database level
  - Add pagination for large result sets
  - Consider materialized views for complex aggregations

**HTML helper complexity:**
- Problem: `BrowseHtmlHelper` (772 lines), `TrendHtmlHelper` (381 lines) generate complex HTML with nested loops
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/controller/browse/BrowseHtmlHelper.java`
- Cause: All HTML generation done in Java code; string concatenation and nested logic
- Improvement path:
  - Migrate to templating engine (Thymeleaf, Freemarker)
  - Separate view logic from business logic
  - Implement client-side rendering for interactive elements

**Async cache refresh blocks thread pool:**
- Problem: Single-threaded async refresh using `new Thread()` could starve other operations
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/cache/AbstractAsyncCache.java` (line 107)
- Cause: No thread pool; each cache refresh spawns dedicated thread
- Improvement path:
  - Use ScheduledExecutorService with bounded queue
  - Implement cache refresh scheduling strategy
  - Monitor queue depth and cache hit rates

**S3 transfer retry without backoff:**
- Problem: S3 transfer retries happen immediately without exponential backoff
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/S3Service.java` (line 55)
- Cause: Fixed retry count but no delay between attempts
- Improvement path:
  - Implement exponential backoff
  - Use S3TransferManager's built-in retry configuration
  - Add metrics for retry counts

## Fragile Areas

**JOOQ generated code dependency:**
- Files: `reportcard-server/src/generated/java/io/github/ericdriggs/reportcard/gen/db/`
- Why fragile: Generated code is never edited but frequently regenerated; schema changes require manual regeneration and careful synchronization
- Safe modification:
  - Always regenerate JOOQ code after schema changes
  - Test all persist services after regeneration
  - Review migration with full integration test suite
- Test coverage: Integration tests use Testcontainers; ensure complete coverage before schema updates

**StagePathPersistService race condition:**
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StagePathPersistService.java`
- Why fragile: Concurrent insert operations on same build stage path could fail; recovery not tested
- Safe modification:
  - Add database-level unique constraints
  - Wrap inserts in transaction with conflict handling
  - Add test simulating concurrent access
- Test coverage: No test for concurrent insert scenario (noted in code TODO)

**Synchronized string-based locks:**
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/BrowseService.java` (line 608)
- Why fragile: String locks don't work as expected; synchronization not effective
- Safe modification:
  - Replace with Object-based locks
  - Use ConcurrentHashMap with proper locking strategy
  - Consider AtomicReference for compare-and-set operations
- Test coverage: Add concurrency test for jobRunMap population

**Cache initialization race condition:**
- Files: `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/TestStatus.java` (line 46), `FaultContext.java` (lines 44, 52)
- Why fragile: Synchronized block on string literal; multiple cache initialization paths
- Safe modification:
  - Use AtomicReference or double-checked locking correctly
  - Refactor to single initialization point
  - Add test verifying thread-safe initialization
- Test coverage: Missing concurrency tests

## Scaling Limits

**Thread-per-cache-refresh model:**
- Current capacity: Limited by JVM thread count (~2000 on default)
- Limit: Each async cache refresh creates new thread; under high concurrency, could exceed thread limits
- Scaling path:
  - Implement thread pool with bounded queue
  - Add metrics for thread pool utilization
  - Implement backpressure/queue rejection policy
  - Consider reactive framework (Project Reactor)

**Database connection pooling:**
- Current capacity: Default HikariCP pool (10 connections)
- Limit: High concurrency could exhaust pool; queries block waiting for connections
- Scaling path:
  - Tune HikariCP pool size based on load testing
  - Monitor connection utilization metrics
  - Implement query timeout policy
  - Consider read replicas for read-heavy workloads

**S3 concurrent upload limit:**
- Current capacity: `S3TransferManager` default multipart threshold
- Limit: Large test result uploads could be slow; no parallel upload indication
- Scaling path:
  - Configure multipart upload settings
  - Implement upload progress tracking
  - Consider batch compression before upload
  - Add metrics for S3 operation latency

**Memory usage with large test result sets:**
- Current capacity: Large test result JSONs stored in single database TEXT column
- Limit: OOM on very large test suites; no streaming/pagination of results
- Scaling path:
  - Implement result chunking/pagination
  - Add compression for stored JSON
  - Consider external blob storage for test case details
  - Profile memory usage under realistic loads

## Dependencies at Risk

**Java 17 compatibility:**
- Risk: Spring Boot 2.6.15 is older version; Java 17 bytecode compatibility concerns
- Impact: Future JVM updates could break compatibility
- Migration plan: Upgrade to Spring Boot 3.x which has full Java 17+ support

**JOOQ 3.x with outdated JAXB:**
- Risk: JAXB 2.3.1 is old; Java 11+ no longer includes JAXB by default
- Impact: Future Java versions could drop JAXB support entirely
- Migration plan: Evaluate JOOQ 3.18+ for better modern Java support

**Deprecated JunitFileMergerUtil:**
- Risk: `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/converter/merge/JunitFileMergerUtil.java` marked @Deprecated with no replacement
- Impact: Callers will generate compiler warnings; functionality unclear if still needed
- Migration plan: Either remove if unused or provide replacement implementation

**AWS SDK v2.23.3:**
- Risk: SDK version is from 2023; AWS regularly releases security patches
- Impact: Potential security vulnerabilities; missing performance improvements
- Migration plan: Regular dependency updates; establish upgrade cadence

## Missing Critical Features

**No backup/disaster recovery:**
- Problem: No documented backup strategy for MySQL database or S3 test artifacts
- Blocks: Cannot guarantee data durability; recovery from database failure unclear
- Recommendations:
  - Implement automated MySQL backups (daily snapshots)
  - Enable S3 versioning and lifecycle policies
  - Document restore procedures
  - Test backup restoration regularly

**No metrics/monitoring:**
- Problem: No metrics collection (Micrometer, Prometheus) for operation monitoring
- Blocks: Cannot observe system health; difficult to debug performance issues in production
- Recommendations:
  - Add Micrometer for Spring Boot metrics
  - Export to Prometheus/CloudWatch
  - Create dashboards for key metrics (query latency, cache hit rate, S3 operations)
  - Set up alerts for resource exhaustion

**No request tracing:**
- Problem: No distributed tracing (Spring Cloud Sleuth/OpenTelemetry) across service calls
- Blocks: Cannot track requests across cache/DB/S3 operations
- Recommendations:
  - Implement Spring Cloud Sleuth
  - Export to Jaeger or AWS X-Ray
  - Add trace ID to all logs

**No rate limiting:**
- Problem: No rate limiting on API endpoints
- Blocks: Cannot protect against abuse; no fair resource allocation
- Recommendations:
  - Implement rate limiting (Spring Cloud Gateway, Bucket4j)
  - Add per-API-key rate limits
  - Implement circuit breakers for S3 failures

## Test Coverage Gaps

**Concurrent insert race condition:**
- What's not tested: Race condition on buildstage path insert when multiple threads attempt simultaneously
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/persist/StagePathPersistService.java` (line 210)
- Risk: Silent failures or inconsistent data states; no verification that recovery works
- Priority: High

**Thread-safe cache initialization:**
- What's not tested: Concurrent initialization of TestStatus and FaultContext caches with string-based synchronization
- Files:
  - `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/TestStatus.java` (line 46)
  - `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/model/FaultContext.java` (lines 44, 52)
- Risk: Multiple threads could initialize caches independently; data inconsistency
- Priority: High

**S3 upload failure recovery:**
- What's not tested: S3 upload failures and retry logic; exception handling during multipart upload
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/S3Service.java` (lines 129-136)
- Risk: Upload failures could silently fail with only retry count protection
- Priority: Medium

**Large file handling:**
- What's not tested: Upload/processing of files near the 200MB limit; extraction and storage of large tar.gz files
- Files: `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/storage/S3Service.java`, `controller/JunitController.java`
- Risk: Memory exhaustion or truncation of large uploads
- Priority: Medium

**Exception handling paths:**
- What's not tested: Specific exception scenarios in converters (malformed XML, missing required fields)
- Files:
  - `reportcard-model/src/main/java/io/github/ericdriggs/reportcard/xml/ResultCount.java` (line 113)
  - `reportcard-server/src/main/java/io/github/ericdriggs/reportcard/model/converter/` (multiple files)
- Risk: Generic RuntimeException wrapping makes testing specific error conditions difficult
- Priority: Medium

---

*Concerns audit: 2026-01-26*
