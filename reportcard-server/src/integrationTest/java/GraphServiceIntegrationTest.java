import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.persist.GraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = ReportcardApplication.class)
@ActiveProfiles("integrationTest")
@TestPropertySource(locations = "classpath:application-integration-test.properties")
@Slf4j
public class GraphServiceIntegrationTest {

    private final GraphService graphService;

    @Autowired
    public GraphServiceIntegrationTest(GraphService graphService) {
        this.graphService = graphService;
    }

}
