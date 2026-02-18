package io.github.ericdriggs.reportcard.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for tag truncation in TestResultModel.
 */
class TestResultModelTagsTest {

    @Test
    void truncateTags_shortTags_unchanged() {
        List<String> tags = Arrays.asList("smoke", "env=prod", "regression");
        List<String> result = TestResultModel.truncateTags(tags);

        assertEquals(3, result.size());
        assertEquals("smoke", result.get(0));
        assertEquals("env=prod", result.get(1));
        assertEquals("regression", result.get(2));
    }

    @Test
    void truncateTags_longTag_truncatedTo25() {
        String longTag = "this-is-a-very-long-tag-that-exceeds-25-characters";
        List<String> tags = Collections.singletonList(longTag);

        List<String> result = TestResultModel.truncateTags(tags);

        assertEquals(1, result.size());
        assertEquals(25, result.get(0).length());
        assertEquals("this-is-a-very-long-tag-t", result.get(0));
    }

    @Test
    void truncateTags_exactly25Chars_unchanged() {
        String exactTag = "exactly-25-characters-tag"; // exactly 25 chars
        assertEquals(25, exactTag.length());

        List<String> result = TestResultModel.truncateTags(Collections.singletonList(exactTag));

        assertEquals(1, result.size());
        assertEquals(exactTag, result.get(0));
    }

    @Test
    void truncateTags_mixedLengths_onlyLongTruncated() {
        List<String> tags = Arrays.asList(
                "short",
                "this-tag-is-way-too-long-for-the-index",
                "medium-length-tag"
        );

        List<String> result = TestResultModel.truncateTags(tags);

        assertEquals(3, result.size());
        assertEquals("short", result.get(0));
        assertEquals(25, result.get(1).length());
        assertEquals("this-tag-is-way-too-long-", result.get(1));
        assertEquals("medium-length-tag", result.get(2));
    }

    @Test
    void truncateTags_nullList_returnsNull() {
        assertNull(TestResultModel.truncateTags(null));
    }

    @Test
    void truncateTags_emptyList_returnsEmpty() {
        List<String> result = TestResultModel.truncateTags(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void truncateTags_nullElement_skipped() {
        List<String> tags = Arrays.asList("smoke", null, "regression");

        List<String> result = TestResultModel.truncateTags(tags);

        assertEquals(2, result.size());
        assertEquals("smoke", result.get(0));
        assertEquals("regression", result.get(1));
    }

    @Test
    void setTagsList_longTags_truncatedInJson() {
        TestResultModel model = new TestResultModel();
        String longTag = "this-is-a-tag-longer-than-25-characters";

        model.setTagsList(Collections.singletonList(longTag));

        String json = model.getTags();
        assertNotNull(json);
        // Should contain truncated tag, not full tag
        assertTrue(json.contains("this-is-a-tag-longer-than"));
        assertFalse(json.contains("this-is-a-tag-longer-than-25-characters"));
    }

    @Test
    void maxTagLength_is25() {
        assertEquals(25, TestResultModel.MAX_TAG_LENGTH);
    }
}
