package com.github.ericdriggs.reportcard.gen.db;


import com.github.ericdriggs.reportcard.ReportCardService;
import com.github.ericdriggs.reportcard.ReportcardApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@SuppressWarnings("PMD")
public abstract class AbstractDbTest {

    protected final ReportCardService reportCardService;

    @Autowired
    public AbstractDbTest(ReportCardService reportCardService ) {
        this.reportCardService = reportCardService;
    }
}
