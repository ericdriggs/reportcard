package io.github.ericdriggs.reportcard.util.db;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.SQLDialect;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("application", "foo' OR '1'='1");
        String sql = condition.toString();

        assertTrue(sql.contains("JSON_EXTRACT"));
        assertNotNull(condition);
    }

    @Test
    void testValueUsesBindParameter() {
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("application", "myvalue");
        DSLContext ctx = DSL.using(SQLDialect.MYSQL);
        String renderedSql = ctx.renderNamedParams(condition);

        // DSL.val() renders as a named parameter placeholder in the SQL sent to DB
        assertTrue(renderedSql.contains(":"), "Value should render as a named bind parameter");
        assertFalse(renderedSql.contains("'myvalue'"), "Value should not be inlined in rendered SQL");
    }

    @Test
    void testWildcardValueUsesBindParameter() {
        Condition condition = SqlJsonUtil.jobInfoContainsKeyValue("application", "my%value");
        DSLContext ctx = DSL.using(SQLDialect.MYSQL);
        String renderedSql = ctx.renderNamedParams(condition);

        assertTrue(renderedSql.contains(":"), "Wildcard value should render as a named bind parameter");
        assertFalse(renderedSql.contains("'my%value'"), "Wildcard value should not be inlined in rendered SQL");
        assertTrue(renderedSql.toLowerCase().contains("like"));
    }

    @Test
    void testJobInfoKeyInValues_InvalidKey() {
        assertThrows(IllegalArgumentException.class,
            () -> SqlJsonUtil.jobInfoKeyInValues("app-name", List.of("foo")));
        assertThrows(IllegalArgumentException.class,
            () -> SqlJsonUtil.jobInfoKeyInValues("app.name", List.of("foo")));
        assertThrows(IllegalArgumentException.class,
            () -> SqlJsonUtil.jobInfoKeyInValues("app name", List.of("foo")));
    }

    @Test
    void testJobInfoKeyInValues_EmptyInputs() {
        assertEquals("(true)", SqlJsonUtil.jobInfoKeyInValues(null, List.of("foo")).toString());
        assertEquals("(true)", SqlJsonUtil.jobInfoKeyInValues("", List.of("foo")).toString());
        assertEquals("(true)", SqlJsonUtil.jobInfoKeyInValues("pipeline", null).toString());
        assertEquals("(true)", SqlJsonUtil.jobInfoKeyInValues("pipeline", List.of()).toString());
    }

    @Test
    void testJobInfoKeyInValues() {
        Condition condition = SqlJsonUtil.jobInfoKeyInValues("pipeline", List.of("bat", "build_acceptance"));
        DSLContext ctx = DSL.using(SQLDialect.MYSQL);
        String sql = ctx.renderInlined(condition);

        assertTrue(sql.contains("JSON_EXTRACT"), "Should use JSON_EXTRACT, got: " + sql);
        assertTrue(sql.contains("pipeline"), "Should reference key 'pipeline', got: " + sql);
        assertTrue(sql.contains("'bat'") || sql.contains("bat"), "Should contain value 'bat', got: " + sql);
        assertTrue(sql.contains("'build_acceptance'") || sql.contains("build_acceptance"), "Should contain value 'build_acceptance', got: " + sql);
        assertTrue(sql.toLowerCase().contains(" in "), "Should use IN clause, got: " + sql);
        assertFalse(sql.toLowerCase().contains("like"), "Should NOT use LIKE, got: " + sql);
    }
}
