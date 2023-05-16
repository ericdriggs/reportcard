package io.github.ericdriggs.reportcard.gen.db;


import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.TestResultUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@SuppressWarnings("PMD")
public abstract class AbstractUploadDbTest {

    protected final TestResultUploadService uploadService;

    @Autowired
    public AbstractUploadDbTest(TestResultUploadService uploadService ) {
        this.uploadService = uploadService;
    }
}
