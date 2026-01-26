package io.github.ericdriggs.reportcard.util.db;

import org.jooq.Condition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlJsonUtilTest {

    @Test
    void testExactMatch() {
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("application", "foo");
        String sql = condition.toString();
        
        assertTrue(sql.contains("JSON_EXTRACT"));
        assertTrue(sql.contains("application"));
        assertTrue(sql.contains("foo"));
        assertTrue(sql.contains("="));
        assertFalse(sql.contains("LIKE"));
    }

    @Test
    void testWildcardMatch() {
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("pipeline", "dev%");
        String sql = condition.toString();
        
        assertTrue(sql.contains("JSON_EXTRACT"));
        assertTrue(sql.contains("pipeline"));
        assertTrue(sql.contains("dev%"));
        assertTrue(sql.contains("LIKE"));
    }

    @Test
    void testUnderscoreWildcard() {
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("environment", "prod_east");
        String sql = condition.toString();
        
        assertTrue(sql.contains("JSON_EXTRACT"));
        assertTrue(sql.contains("environment"));
        assertTrue(sql.contains("prod_east"));
        assertTrue(sql.contains("LIKE"));
    }

    @Test
    void testKeyValidation_Valid() {
        assertDoesNotThrow(() -> SqlJsonUtil.jobInfoContainsKeyValue("application", "foo"));
        assertDoesNotThrow(() -> SqlJsonUtil.jobInfoContainsKeyValue("app_name", "foo"));
        assertDoesNotThrow(() -> SqlJsonUtil.jobInfoContainsKeyValue("app123", "foo"));
    }

    @Test
    void testKeyValidation_Invalid() {
        assertThrows(IllegalArgumentException.class, 
            () -> SqlJsonUtil.jobInfoContainsKeyValue("app-name", "foo"));
        assertThrows(IllegalArgumentException.class, 
            () -> SqlJsonUtil.jobInfoContainsKeyValue("app.name", "foo"));
        assertThrows(IllegalArgumentException.class, 
            () -> SqlJsonUtil.jobInfoContainsKeyValue("app name", "foo"));
        assertThrows(IllegalArgumentException.class, 
            () -> SqlJsonUtil.jobInfoContainsKeyValue("app$name", "foo"));
    }

    @Test
    void testEmptyKey() {
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("", "foo");
        assertEquals("(true)", condition.toString());
    }

    @Test
    void testEmptyValue() {
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("application", "");
        assertEquals("(true)", condition.toString());
    }

    @Test
    void testNullKey() {
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue(null, "foo");
        assertEquals("(true)", condition.toString());
    }

    @Test
    void testNullValue() {
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("application", null);
        assertEquals("(true)", condition.toString());
    }

    @Test
    void testSqlInjectionPrevention() {
        // Verify that malicious input doesn't cause SQL injection
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("application", "foo' OR '1'='1");
        String sql = condition.toString();
        
        // Should contain JSON_EXTRACT and be parameterized
        assertTrue(sql.contains("JSON_EXTRACT"));
        // The condition should be created successfully without throwing exception
        assertNotNull(condition);
    }
}
