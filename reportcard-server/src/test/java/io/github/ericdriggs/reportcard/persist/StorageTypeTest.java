package io.github.ericdriggs.reportcard.persist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageTypeTest {

    @Test
    void toLabel_appendsLowercaseTypeSuffix() {
        assertEquals("cucumber_html_tar_gz", StorageType.TAR_GZ.toLabel("cucumber_html"));
        assertEquals("test_html", StorageType.HTML.toLabel("test"));
        assertEquals("report_json", StorageType.JSON.toLabel("report"));
    }

    @Test
    void toLabel_withEmptyBaseLabel() {
        assertEquals("_tar_gz", StorageType.TAR_GZ.toLabel(""));
    }
}
