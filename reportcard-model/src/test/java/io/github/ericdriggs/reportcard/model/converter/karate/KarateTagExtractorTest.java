package io.github.ericdriggs.reportcard.model.converter.karate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KarateTagExtractorTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final KarateTagExtractor extractor = new KarateTagExtractor();

    @Test
    void testExtractTags_nullInput() {
        List<String> result = extractor.extractTags(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractTags_emptyArray() {
        ArrayNode emptyArray = objectMapper.createArrayNode();
        List<String> result = extractor.extractTags(emptyArray);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractTags_nonArrayInput() {
        JsonNode nonArray = objectMapper.createObjectNode().put("foo", "bar");
        List<String> result = extractor.extractTags(nonArray);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractTags_singleSimpleTag() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@smoke");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(1, result.size());
        assertEquals("smoke", result.get(0));
    }

    @Test
    void testExtractTags_tagWithoutAtPrefix() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "smoke");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(1, result.size());
        assertEquals("smoke", result.get(0));
    }

    @Test
    void testExtractTags_tagWithKeyValue() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@env=staging");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(1, result.size());
        assertEquals("env=staging", result.get(0));
    }

    @Test
    void testExtractTags_tagWithWhitespace() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@ smoke ");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(1, result.size());
        assertEquals("smoke", result.get(0));
    }

    @Test
    void testExtractTags_tagWithKeyValueWhitespace() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@ env = staging ");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(1, result.size());
        assertEquals("env=staging", result.get(0));
    }

    @Test
    void testExtractTags_commaExpansionTwoValues() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@env=dev,test");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(2, result.size());
        assertEquals("env=dev", result.get(0));
        assertEquals("env=test", result.get(1));
    }

    @Test
    void testExtractTags_commaExpansionWithSpaces() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@env=dev, test");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(2, result.size());
        assertEquals("env=dev", result.get(0));
        assertEquals("env=test", result.get(1));
    }

    @Test
    void testExtractTags_commaExpansionThreeValues() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@env=dev,test,prod");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(3, result.size());
        assertEquals("env=dev", result.get(0));
        assertEquals("env=test", result.get(1));
        assertEquals("env=prod", result.get(2));
    }

    @Test
    void testExtractTags_multipleEqualsInValue() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@foo=bar=baz");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(1, result.size());
        assertEquals("foo=bar=baz", result.get(0));
    }

    @Test
    void testExtractTags_commaWithoutEquals() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@smoke,regression");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> extractor.extractTags(tagsArray)
        );
        assertTrue(exception.getMessage().contains("Comma without ="));
    }

    @Test
    void testExtractTags_multipleTags() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@smoke");
        tagsArray.addObject().put("name", "@regression");
        tagsArray.addObject().put("name", "@env=staging");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(3, result.size());
        assertEquals("smoke", result.get(0));
        assertEquals("regression", result.get(1));
        assertEquals("env=staging", result.get(2));
    }

    @Test
    void testExtractTags_deduplication() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@smoke");
        tagsArray.addObject().put("name", "@regression");
        tagsArray.addObject().put("name", "@smoke");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(2, result.size());
        assertEquals("smoke", result.get(0));
        assertEquals("regression", result.get(1));
    }

    @Test
    void testExtractTags_tagObjectWithoutName() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("line", 1); // missing "name" field

        List<String> result = extractor.extractTags(tagsArray);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractTags_emptyTagName() {
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "");

        List<String> result = extractor.extractTags(tagsArray);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractTags_commaExpansionMixedFormat() {
        // First part has =, subsequent parts some have = some don't
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject().put("name", "@env=dev,staging=special,prod");

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(3, result.size());
        assertEquals("env=dev", result.get(0));
        assertEquals("staging=special", result.get(1));
        assertEquals("env=prod", result.get(2));
    }

    @Test
    void testExtractTags_realWorldExample() {
        // Example from Karate JSON
        ArrayNode tagsArray = objectMapper.createArrayNode();
        tagsArray.addObject()
            .put("name", "@envnot=staging-cp1-us-east-2")
            .put("line", 1);
        tagsArray.addObject()
            .put("name", "@smoke")
            .put("line", 1);

        List<String> result = extractor.extractTags(tagsArray);
        assertEquals(2, result.size());
        assertEquals("envnot=staging-cp1-us-east-2", result.get(0));
        assertEquals("smoke", result.get(1));
    }
}
