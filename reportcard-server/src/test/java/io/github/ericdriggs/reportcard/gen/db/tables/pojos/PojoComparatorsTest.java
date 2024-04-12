package io.github.ericdriggs.reportcard.gen.db.tables.pojos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PojoComparatorsTest {

    @Test
    void jobInfoDifferentOrderTest() {
        final String json1 = """
                             {"PIPELINE": "pipeline1", "APPLICATION": "app1"}
                             """;

        final String json2 = """
                             {"APPLICATION": "app1", "PIPELINE": "pipeline1"}
                             """;
        JobPojo jp1 = JobPojo.builder().jobInfo(json1).build();
        JobPojo jp2 = JobPojo.builder().jobInfo(json2).build();
        assertEquals(0, PojoComparators.compareJob(jp1, jp2));
    }
}
