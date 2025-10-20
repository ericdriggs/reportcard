import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import io.github.ericdriggs.reportcard.model.graph.TestSuiteGraph;
import io.github.ericdriggs.reportcard.persist.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

//This is a manual integration test which was used to migrate form relational test results to json
//The relational test data was far too slow since it performed too many joins
@SpringBootTest(classes = ReportcardApplication.class)
@ActiveProfiles("integrationTest")
@TestPropertySource(locations = "classpath:application-integration-test.properties")
@Slf4j
@Disabled
public class TestResultJsonUpdateIntegrationTest {

    private final GraphService graphService;

    @Autowired
    public TestResultJsonUpdateIntegrationTest(GraphService graphService) {
        this.graphService = graphService;
    }

    @Test
    void populateTestSuitesJson() {
        final Instant start = Instant.now();
        final int maxSize = 1;
        int count = 0;
        try {
            Set<Long> testResultIds = graphService.getTestResultsWithoutSuiteJson(maxSize);
            log.info("testResultIds: " + testResultIds);
            for (Long testResultId : testResultIds) {
                graphService.populateTestSuitesJson(testResultId);
                count++;
            }
        } finally {
            final Instant end = Instant.now();
            final Duration elapsed = Duration.between(start, end);
            final Long durationMillis = elapsed.toMillis();

            log.info("count: {}, elapsed time:{} ", count, durationMillis);
            BigDecimal averageDurationMillis = BigDecimal.valueOf((float) durationMillis / ((float) count)).setScale(0, RoundingMode.HALF_UP);
            log.info("Duration: {}, durationMillis: {}", elapsed, durationMillis);
            log.info("Count: {}, average duration millis: {}", count, averageDurationMillis);
        }
    }

    @Test
    void getTestSuitesJsonForId() {
        Long testResultId = 4937L;
        List<TestSuiteGraph> testSuitesGraph = graphService.getTestSuitesGraph(testResultId);
        log.info("testSuitesGraph: {}", SharedObjectMappers.writeValueAsString(testSuitesGraph));
    }

}
