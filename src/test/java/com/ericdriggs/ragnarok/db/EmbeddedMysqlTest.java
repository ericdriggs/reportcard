package com.ericdriggs.ragnarok.db;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;

@Profile("test")
public class EmbeddedMysqlTest extends AbstractDbTest {

    @Test
    public void foo() {

    }
}
