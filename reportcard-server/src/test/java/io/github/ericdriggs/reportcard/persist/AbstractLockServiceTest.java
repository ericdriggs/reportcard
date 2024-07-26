package io.github.ericdriggs.reportcard.persist;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@SuppressWarnings("PMD")
public abstract class AbstractLockServiceTest {

    protected final ObjectMapper objectMapper = SharedObjectMappers.simpleObjectMapper;

    protected final LockService lockService;

    @Autowired
    public AbstractLockServiceTest(LockService lockService) {
        this.lockService = lockService;
    }
}
