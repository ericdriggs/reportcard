package com.ericdriggs.reportcard.db;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("test")
//@EnableConfigurationProperties
public class EmbeddedMysqlTest extends AbstractDbTest {

    @Test
    public void foo() {

    }
}
