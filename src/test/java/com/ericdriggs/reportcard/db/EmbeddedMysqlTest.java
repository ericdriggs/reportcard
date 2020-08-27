package com.ericdriggs.reportcard.db;

import com.ericdriggs.reportcard.ReportCardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("test")
//@EnableConfigurationProperties
public class EmbeddedMysqlTest extends AbstractDbTest {

    @Autowired
    public EmbeddedMysqlTest(ReportCardService reportCardService ) {
        super(reportCardService);
    }

    @Test
    public void getBuildStagePath() {
        reportCardService.getBuildStagePath("default", "default", "app1", "master", 1, "unit");
        int i = 54;
    }
}
