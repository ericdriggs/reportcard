package io.github.ericdriggs.reportcard.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.persist.FailedPublicationSelectionTest;
import io.github.ericdriggs.reportcard.persist.StoragePersistService;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.storage.S3Service;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static io.github.ericdriggs.reportcard.gen.db.Tables.STORAGE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class FailedPublicationControllerTest {

    static final String JSON_ROUTE = "/v1/api/failed-publishes";
    static final String HTML_ROUTE = "/failed-publishes";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    StoragePersistService storagePersistService;

    @Autowired
    TestResultPersistService testResultPersistService;

    @Autowired
    S3Service s3Service;

    @Autowired
    ResourceReaderComponent resourceReader;

    @Autowired
    DSLContext dsl;

    final ObjectMapper mapper = new ObjectMapper();

    StagePath seed(String company, String stageName, Instant runDate, List<String> labels) {
        return FailedPublicationSelectionTest.seedStage(storagePersistService, testResultPersistService, dsl,
                company, stageName, runDate, labels, false);
    }

    static String uniqueCompany(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    void seedBoundary(String company) {
        seed(company, "boundary", Instant.now().minus(Duration.ofDays(3)), List.of("junit"));
    }

    ResponseEntity<String> get(String url) {
        return restTemplate.getForEntity(url, String.class);
    }

    JsonNode okJson(String url) throws IOException {
        final ResponseEntity<String> response = get(url);
        assertEquals(200, response.getStatusCodeValue(), url + " -> " + response.getBody());
        assertTrue(MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType()));
        return mapper.readTree(response.getBody());
    }

    static List<Long> stageIds(JsonNode body) {
        return StreamSupport.stream(body.get("failedPublications").spliterator(), false)
                .map(row -> row.get("stageId").asLong())
                .toList();
    }

    static Long stageId(StagePath stagePath) {
        return stagePath.getStage().getStageId();
    }

    Map<String, StoragePojo> storagesByLabel(Long stageId) {
        return dsl.selectFrom(STORAGE).where(STORAGE.STAGE_FK.eq(stageId)).fetchInto(StoragePojo.class).stream()
                .collect(Collectors.toMap(StoragePojo::getLabel, s -> s));
    }

    void completeJunitUpload(StagePath stagePath) throws IOException {
        final StoragePojo junit = storagesByLabel(stageId(stagePath)).get("junit");
        s3Service.uploadTarGz(junit.getPrefix(), false, JunitControllerTest.getJunitTarGz(resourceReader));
        storagePersistService.setUploadCompleted(junit.getIndexFile(), junit.getLabel(), junit.getPrefix(), stageId(stagePath));
    }

    /**
     * Given suspected stages dated 30 minutes, 5 minutes, and 25 hours ago,
     * When GET /v1/api/failed-publishes is called without parameters,
     * Expected 200 JSON echoing days=1, olderThanMinutes=10, limit=50 and cutoffs one day and ten minutes before
     * the request, including only the 30-minute-old stage.
     */
    @Test
    void whenJsonDefaultsAreUsed_expectEchoedDefaultsAndRunDateWindow() throws IOException {
        final String company = uniqueCompany("fpdefaults");
        seedBoundary(company);
        final StagePath included = seed(company, "thirtyMinutesOld", Instant.now().minus(Duration.ofMinutes(30)), List.of("junit"));
        final StagePath tooRecent = seed(company, "fiveMinutesOld", Instant.now().minus(Duration.ofMinutes(5)), List.of("junit"));
        final StagePath tooOld = seed(company, "twentyFiveHoursOld", Instant.now().minus(Duration.ofHours(25)), List.of("junit"));

        final Instant before = Instant.now();
        final JsonNode body = okJson(JSON_ROUTE);
        final Instant after = Instant.now();

        assertEquals(1, body.get("days").asInt());
        assertEquals(10, body.get("olderThanMinutes").asInt());
        assertEquals(50, body.get("limit").asInt());
        final Instant dateCutoff = Instant.parse(body.get("dateCutoff").asText());
        final Instant graceCutoff = Instant.parse(body.get("graceCutoff").asText());
        assertFalse(dateCutoff.isBefore(before.minus(Duration.ofDays(1)).minusSeconds(1)));
        assertFalse(dateCutoff.isAfter(after.minus(Duration.ofDays(1)).plusSeconds(1)));
        assertFalse(graceCutoff.isBefore(before.minus(Duration.ofMinutes(10)).minusSeconds(1)));
        assertFalse(graceCutoff.isAfter(after.minus(Duration.ofMinutes(10)).plusSeconds(1)));

        final List<Long> ids = stageIds(body);
        assertTrue(ids.contains(stageId(included)), "30-minute-old stage must be reported");
        assertFalse(ids.contains(stageId(tooRecent)), "stage inside the grace period must be excluded");
        assertFalse(ids.contains(stageId(tooOld)), "stage older than days must be excluded");
    }

    /**
     * Given 51 suspected stages dated 30 minutes ago,
     * When GET /v1/api/failed-publishes is called without limit and then with limit=2,
     * Expected the default returns exactly the 50 newest stages newest first, and limit=2 returns the 2 newest.
     */
    @Test
    void whenDefaultLimitApplies_expectFiftyNewestStagesAndExplicitLimitHonored() throws IOException {
        final String company = uniqueCompany("fplimit");
        seedBoundary(company);
        final Instant runDate = Instant.now().minus(Duration.ofMinutes(30));
        final List<Long> seeded = IntStream.range(0, 51)
                .mapToObj(i -> stageId(seed(company, "limit" + i, runDate, List.of("junit"))))
                .toList();
        final List<Long> newestFirst = seeded.stream().sorted(Comparator.reverseOrder()).toList();

        assertEquals(newestFirst.subList(0, 50), stageIds(okJson(JSON_ROUTE)));
        assertEquals(newestFirst.subList(0, 2), stageIds(okJson(JSON_ROUTE + "?limit=2")));
    }

    /**
     * Given the JSON and HTML failed-publication views,
     * When either is called with days 0 or 31, limit 0 or 201, olderThanMinutes 0, or a non-numeric days, limit, or
     * olderThanMinutes value,
     * Expected 400 Bad Request.
     */
    @Test
    void whenParameterValuesAreInvalid_expectBadRequestOnBothViews() {
        final List<String> invalid = List.of("days=0", "days=31", "limit=0", "limit=201", "olderThanMinutes=0",
                "days=abc", "limit=abc", "olderThanMinutes=abc");
        for (String route : List.of(JSON_ROUTE, HTML_ROUTE)) {
            for (String query : invalid) {
                assertEquals(400, get(route + "?" + query).getStatusCodeValue(), route + "?" + query);
            }
        }
    }

    /**
     * Given a suspected stage dated 30 minutes ago,
     * When the JSON and HTML views are called with query parameters other than days, olderThanMinutes, and limit
     * (company, org, and before-run-id),
     * Expected 200 on both views, the JSON view echoes the defaults and returns the same global stage list as the
     * request without those parameters, and the HTML view still renders the fixture stage row.
     */
    @Test
    void whenUnknownParametersAreSent_expectThemIgnoredAndResultsGlobal() throws IOException {
        final String company = uniqueCompany("fpunknown");
        seedBoundary(company);
        final StagePath suspected = seed(company, "unknownParams", Instant.now().minus(Duration.ofMinutes(30)), List.of("junit"));
        final String unknown = "company=acme&org=unknown-org&before-run-id=1";

        final JsonNode plain = okJson(JSON_ROUTE);
        final JsonNode withUnknown = okJson(JSON_ROUTE + "?" + unknown);
        assertEquals(1, withUnknown.get("days").asInt());
        assertEquals(10, withUnknown.get("olderThanMinutes").asInt());
        assertEquals(50, withUnknown.get("limit").asInt());
        assertTrue(stageIds(withUnknown).contains(stageId(suspected)), "company=acme must not scope the results");
        assertEquals(stageIds(plain), stageIds(withUnknown));

        final ResponseEntity<String> html = get(HTML_ROUTE + "?" + unknown);
        assertEquals(200, html.getStatusCodeValue());
        assertTrue(html.getBody().contains("data-stage-id=\"" + stageId(suspected) + "\""));
    }

    /**
     * Given the JSON and HTML failed-publication views,
     * When either is called with days 1 or 30, limit 1 or 200, or olderThanMinutes 1,
     * Expected 200 OK, and the JSON view echoes the accepted value.
     */
    @Test
    void whenParametersAreAtTheirBounds_expectOkOnBothViews() throws IOException {
        final Map<String, Integer> accepted = new LinkedHashMap<>();
        accepted.put("days=1", 1);
        accepted.put("days=30", 30);
        accepted.put("limit=1", 1);
        accepted.put("limit=200", 200);
        accepted.put("olderThanMinutes=1", 1);
        for (Map.Entry<String, Integer> entry : accepted.entrySet()) {
            final String name = entry.getKey().substring(0, entry.getKey().indexOf('='));
            assertEquals(entry.getValue(), okJson(JSON_ROUTE + "?" + entry.getKey()).get(name).asInt(), entry.getKey());
            assertEquals(200, get(HTML_ROUTE + "?" + entry.getKey()).getStatusCodeValue(), HTML_ROUTE + "?" + entry.getKey());
        }
    }

    /**
     * Given a suspected stage in one company with complete junit and incomplete html storage, followed by a
     * suspected stage in another company with incomplete junit storage,
     * When GET /v1/api/failed-publishes is called,
     * Expected both companies appear (global view), newest stage first, one row per stage with its hierarchy fields,
     * storage grouped by label with completion flags and storage-key URLs, the complete link returns 200, and the
     * retained incomplete link returns 404.
     */
    @Test
    void whenStagesSpanCompanies_expectGlobalNewestFirstRowsWithGroupedStorageLinks() throws IOException {
        final String companyA = uniqueCompany("fpglobal-a");
        final String companyB = uniqueCompany("fpglobal-b");
        seedBoundary(companyA);
        final Instant runDate = Instant.now().minus(Duration.ofMinutes(30));
        final StagePath stageA = seed(companyA, "globalA", runDate, List.of("junit", "html"));
        completeJunitUpload(stageA);
        final StagePath stageB = seed(companyB, "globalB", runDate, List.of("junit"));

        final JsonNode body = okJson(JSON_ROUTE);
        final List<Long> ids = stageIds(body);
        assertEquals(List.of(stageId(stageB), stageId(stageA)), ids.subList(0, 2));
        assertEquals(ids.size(), new HashSet<>(ids).size(), "one row per stage");

        final JsonNode rowA = body.get("failedPublications").get(1);
        assertEquals(companyA, rowA.get("companyName").asText());
        assertEquals(stageA.getOrg().getOrgName(), rowA.get("orgName").asText());
        assertEquals(stageA.getRepo().getRepoName(), rowA.get("repoName").asText());
        assertEquals(stageA.getBranch().getBranchName(), rowA.get("branchName").asText());
        assertEquals(stageA.getJob().getJobId(), rowA.get("jobId").asLong());
        assertTrue(rowA.hasNonNull("jobInfo"));
        assertEquals(stageA.getRun().getRunId(), rowA.get("runId").asLong());
        assertEquals(stageA.getRun().getRunReference(), rowA.get("runReference").asText());
        assertEquals(stageA.getRun().getSha(), rowA.get("sha").asText());
        assertTrue(rowA.hasNonNull("runDate"));
        assertEquals("globalA", rowA.get("stageName").asText());
        assertEquals(companyB, body.get("failedPublications").get(0).get("companyName").asText());

        final Map<String, StoragePojo> storagesA = storagesByLabel(stageId(stageA));
        final JsonNode storages = rowA.get("storages");
        assertEquals(Set.of("junit", "html"), Set.copyOf(iteratorToList(storages.fieldNames())));
        for (String label : List.of("junit", "html")) {
            final JsonNode storage = storages.get(label);
            final StoragePojo pojo = storagesA.get(label);
            assertEquals(pojo.getStorageId(), storage.get("storageId").asLong());
            assertEquals(label, storage.get("label").asText());
            assertEquals(pojo.getStorageType(), storage.get("storageType").asInt());
            assertEquals(BrowseHtmlHelper.getStorageKey(pojo), storage.get("url").asText());
        }
        assertTrue(storages.get("junit").get("isUploadComplete").asBoolean());
        assertFalse(storages.get("html").get("isUploadComplete").asBoolean());

        assertEquals(200, get(storages.get("junit").get("url").asText()).getStatusCodeValue(), "complete link resolves");
        assertEquals(404, get(storages.get("html").get("url").asText()).getStatusCodeValue(), "incomplete link may be missing");
    }

    /**
     * Given two suspected stages, one with complete junit and incomplete html storage,
     * When GET /failed-publishes is called,
     * Expected 200 HTML containing the failed-publications table, exactly one row per stage, and a labeled link to
     * every storage URL marked complete or incomplete.
     */
    @Test
    void whenHtmlViewIsRequested_expectTableWithOneRowPerStageAndStorageLinks() throws IOException {
        final String company = uniqueCompany("fphtml");
        seedBoundary(company);
        final Instant runDate = Instant.now().minus(Duration.ofMinutes(30));
        final StagePath first = seed(company, "htmlFirst", runDate, List.of("junit", "html"));
        completeJunitUpload(first);
        final StagePath second = seed(company, "htmlSecond", runDate, List.of("junit"));

        final ResponseEntity<String> response = get(HTML_ROUTE);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(MediaType.TEXT_HTML.isCompatibleWith(response.getHeaders().getContentType()));
        final String html = response.getBody();
        assertNotNull(html);
        assertTrue(html.contains("<table class=\"failed-publications\""), "table marker");

        for (StagePath stagePath : List.of(first, second)) {
            final String rowMarker = "data-stage-id=\"" + stageId(stagePath) + "\"";
            assertEquals(1, countOccurrences(html, rowMarker), "one row for stage " + stageId(stagePath));
            for (StoragePojo storage : storagesByLabel(stageId(stagePath)).values()) {
                assertTrue(html.contains("href=\"" + BrowseHtmlHelper.getStorageKey(storage) + "\""), storage.getLabel() + " link");
            }
        }
        assertTrue(html.contains("junit (complete)"));
        assertTrue(html.contains("html (incomplete)"));
        assertTrue(html.contains("junit (incomplete)"));
        assertTrue(html.indexOf("data-stage-id=\"" + stageId(second) + "\"")
                < html.indexOf("data-stage-id=\"" + stageId(first) + "\""), "newest stage first");
    }

    static int countOccurrences(String haystack, String needle) {
        int count = 0;
        for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + needle.length())) {
            count++;
        }
        return count;
    }

    static <T> List<T> iteratorToList(Iterator<T> iterator) {
        final List<T> list = new ArrayList<>();
        iterator.forEachRemaining(list::add);
        return list;
    }
}
