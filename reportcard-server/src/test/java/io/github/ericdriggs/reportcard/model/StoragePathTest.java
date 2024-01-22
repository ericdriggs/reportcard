package io.github.ericdriggs.reportcard.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static io.github.ericdriggs.reportcard.model.StoragePath.dateYmd;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StoragePathTest {

    @Test
    void formatDateTest() {
        Instant then = OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();

        String parsed = dateYmd.format(then);
        assertEquals("2023-01-01", parsed);
    }
}
