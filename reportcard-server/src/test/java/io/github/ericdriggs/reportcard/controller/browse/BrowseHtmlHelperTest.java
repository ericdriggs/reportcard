package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.persist.StorageType;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BrowseHtmlHelper.getReportLinks() method.
 * Verifies conditional rendering of download links for TAR_GZ storage types
 * vs browse links for other storage types.
 */
class BrowseHtmlHelperTest {

    @Test
    void getReportLinks_withHtmlStorageOnly_rendersBrowseLinkWithoutTooltip() {
        StoragePojo htmlStorage = createStoragePojo(
                "cucumber_html",
                "rc/company/org/repo/branch/sha/jobId/runId/stage/cucumber_html",
                "index.html",
                StorageType.HTML.getStorageTypeId()
        );

        Set<StoragePojo> storages = Set.of(htmlStorage);
        String html = BrowseHtmlHelper.getReportLinks(storages);

        // Verify browse link rendered
        assertTrue(html.contains("cucumber_html"), "Should contain label");
        assertTrue(html.contains("/v1/api/storage/key/rc/company/org/repo/branch/sha/jobId/runId/stage/cucumber_html/index.html"),
                "Should contain full URL with index file");
        assertTrue(html.contains("class=\"info report-link\""), "Should have report-link class");
        assertTrue(html.contains("report-simple.svg"), "Should have report icon");

        // Verify NO tooltip on browse link
        assertFalse(html.contains("title="), "Browse link should NOT have tooltip");
    }

    @Test
    void getReportLinks_withTarGzStorageOnly_rendersDownloadLinkWithTooltip() {
        StoragePojo tarGzStorage = createStoragePojo(
                "cucumber_html_tar_gz",
                "rc/company/org/repo/branch/sha/jobId/runId/stage/cucumber_html_tar_gz",
                "storage.tar.gz",
                StorageType.TAR_GZ.getStorageTypeId()
        );

        Set<StoragePojo> storages = Set.of(tarGzStorage);
        String html = BrowseHtmlHelper.getReportLinks(storages);

        // Verify download link rendered with tooltip - display is just indexFile
        assertTrue(html.contains(">storage.tar.gz<"), "Should contain indexFile as display name");
        assertTrue(html.contains("/v1/api/storage/key/rc/company/org/repo/branch/sha/jobId/runId/stage/cucumber_html_tar_gz"),
                "Should contain full URL");
        assertTrue(html.contains("title=\"Download cucumber HTML report as tar.gz archive\""),
                "Should have download tooltip");
        assertTrue(html.contains("class=\"info report-link\""), "Should have report-link class");
        assertTrue(html.contains("report-simple.svg"), "Should have report icon");
    }

    @Test
    void getReportLinks_withBothStorageTypes_rendersBothLinksCorrectly() {
        StoragePojo htmlStorage = createStoragePojo(
                "cucumber_html",
                "rc/test/path/cucumber_html",
                "index.html",
                StorageType.HTML.getStorageTypeId()
        );

        StoragePojo tarGzStorage = createStoragePojo(
                "cucumber_html_tar_gz",
                "rc/test/path/cucumber_html_tar_gz",
                "storage.tar.gz",
                StorageType.TAR_GZ.getStorageTypeId()
        );

        // Use LinkedHashSet to maintain insertion order for predictable testing
        Set<StoragePojo> storages = new LinkedHashSet<>();
        storages.add(htmlStorage);
        storages.add(tarGzStorage);

        String html = BrowseHtmlHelper.getReportLinks(storages);

        // Verify both display names present (just indexFile)
        assertTrue(html.contains(">index.html<"), "Should contain HTML display name");
        assertTrue(html.contains(">storage.tar.gz<"), "Should contain tar.gz display name");

        // Verify tooltip only on tar.gz link
        assertTrue(html.contains("title=\"Download cucumber HTML report as tar.gz archive\""),
                "Tar.gz link should have tooltip");

        // Count occurrences of 'title=' - should be exactly 1 (only on tar.gz)
        int titleCount = countOccurrences(html, "title=");
        assertEquals(1, titleCount, "Only tar.gz link should have title attribute");

        // Verify both URLs are correct
        assertTrue(html.contains("/v1/api/storage/key/rc/test/path/cucumber_html/index.html"),
                "HTML link should have index.html");
        assertTrue(html.contains("/v1/api/storage/key/rc/test/path/cucumber_html_tar_gz"),
                "Tar.gz link URL should not change");
    }

    @Test
    void getReportLinks_withEmptySet_returnsEmptyString() {
        Set<StoragePojo> storages = Set.of();
        String html = BrowseHtmlHelper.getReportLinks(storages);

        assertEquals("", html, "Empty set should return empty string");
    }

    @Test
    void getReportLinks_withNullSet_returnsEmptyString() {
        String html = BrowseHtmlHelper.getReportLinks(null);

        assertEquals("", html, "Null set should return empty string");
    }

    @Test
    void getReportLinks_withNullStorageType_rendersBrowseLink() {
        // Storage with null storageType should be treated as non-tar.gz
        StoragePojo storageWithNullType = createStoragePojo(
                "some_report",
                "rc/test/path/some_report",
                "index.html",
                null // null storage type
        );

        Set<StoragePojo> storages = Set.of(storageWithNullType);
        String html = BrowseHtmlHelper.getReportLinks(storages);

        // Should render as browse link without tooltip
        assertTrue(html.contains("some_report"), "Should contain label");
        assertFalse(html.contains("title="), "Null storageType should NOT have tooltip");
    }

    @Test
    void getReportLinks_withOtherStorageTypes_rendersBrowseLinks() {
        // Test that non-TAR_GZ types all render as browse links
        StoragePojo jsonStorage = createStoragePojo(
                "test_results.json",
                "rc/test/path/test_results.json",
                null,
                StorageType.JSON.getStorageTypeId()
        );

        StoragePojo xmlStorage = createStoragePojo(
                "junit.xml",
                "rc/test/path/junit.xml",
                null,
                StorageType.XML.getStorageTypeId()
        );

        Set<StoragePojo> storages = new LinkedHashSet<>();
        storages.add(jsonStorage);
        storages.add(xmlStorage);

        String html = BrowseHtmlHelper.getReportLinks(storages);

        // Both should render without tooltips
        assertTrue(html.contains("test_results.json"), "Should contain JSON label");
        assertTrue(html.contains("junit.xml"), "Should contain XML label");
        assertFalse(html.contains("title="), "Non-TAR_GZ types should NOT have tooltips");
    }

    @Test
    void getReportLinks_withNullIndexFile_displaysLabelOnly() {
        // Verify that storage with null indexFile displays just the label
        StoragePojo tarGzStorage = createStoragePojo(
                "cucumber_html_tar_gz",
                "rc/test/path/cucumber_html_tar_gz",
                null,
                StorageType.TAR_GZ.getStorageTypeId()
        );

        Set<StoragePojo> storages = Set.of(tarGzStorage);
        String html = BrowseHtmlHelper.getReportLinks(storages);

        // Display name should be just the label (no /null)
        assertTrue(html.contains(">cucumber_html_tar_gz<"), "Display should be just the label");
        assertFalse(html.contains("/null"), "Should not contain /null");
        // URL should end with the prefix
        assertTrue(html.contains("href=\"/v1/api/storage/key/rc/test/path/cucumber_html_tar_gz\""),
                "URL should not have /null or trailing content after prefix");
    }

    // Helper method to create StoragePojo with test data
    private StoragePojo createStoragePojo(String label, String prefix, String indexFile, Integer storageType) {
        StoragePojo storage = new StoragePojo();
        storage.setLabel(label);
        storage.setPrefix(prefix);
        storage.setIndexFile(indexFile);
        storage.setStorageType(storageType);
        return storage;
    }

    // Helper method to count occurrences of a substring
    private int countOccurrences(String str, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
