import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.persist.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@SpringBootTest(classes = ReportcardApplication.class)
@ActiveProfiles("integrationTest")
@TestPropertySource(locations = "classpath:application-integration-test.properties")
@Slf4j
public class TestResultJsonUpdateIntegrationTest {

    private final GraphService graphService;

    @Autowired
    public TestResultJsonUpdateIntegrationTest(GraphService graphService) {
        this.graphService = graphService;
    }

    @Test
    void populateTestSuitesJson() {
        final Instant start = Instant.now();
        final int maxSize = 2000;
        Set<Long> testResultIds = graphService.getTestResultsWithoutSuiteJson(maxSize);
        final int actualSize = testResultIds.size();
        log.info("testResultIds: " + testResultIds);
        for (Long testResultId : testResultIds) {
            graphService.populateTestSuitesJson(testResultId);
        }
        final Instant end = Instant.now();
        final Duration elapsed = Duration.between(start, end);
        final Long durationMillis = elapsed.toMillis();
        BigDecimal averageDurationMillis = new BigDecimal(durationMillis).divide(new BigDecimal(actualSize)).setScale(0);
        log.info("Duration: {}, durationMillis: {}", durationMillis, durationMillis);
        log.info("Count: {}, average duration millis: {}", actualSize, averageDurationMillis);
    }
}
