package org.jooq.codegen;

import org.junit.jupiter.api.Test;

/**
 * Contains a trivial test so that Jacoco won't blow up on aggregating test results.
 *
 */
public class JavaLombokGeneratorTest {
    @Test
    public void construtorTest() {
        new JavaLombokGenerator();
    }
}
