--
-- PostgreSQL DDL generated from JUnit.xsd using xsd2pgschema
--  xsd2pgschema - Database replication tool based on XML Schema
--  https://sourceforge.net/projects/xsd2pgschema/
--
-- Schema modeling options:
--  explicit named schema: false
--  relational extension: true
--  inline simple content: false
--  realize simple bridge: false
--  wild card extension: true
--  case sensitive name: false
--  no name collision: false
--  append document key: false
--  append serial key: false
--  append xpath key: false
--  retain constraint: true
--  retrieve field annotation: false
--  map integer numbers to: big integer
--  map decimal numbers to: big decimal
--  map xsd date type to: sql date type
--  assumed hash algorithm: SHA-1
--  hash key type: unsigned long 64 bits
--
-- Statistics of schema:
--  Generated 10 tables (49 fields), 3 views (6 fields), 0 attr groups, 0 model groups in total
--   Namespaces:
--    http://www.w3.org/2001/XMLSchema (xs)
--   Schema locations:
--    JUnit.xsd
--   Table types:
--    1 root, 1 root children, 2 admin roots, 9 admin children
--   System keys:
--    13 primary keys (5 unique constraints), 7 foreign keys, 15 nested keys (2 as attribute, 0 as attribute group)
--   User keys:
--    0 document keys, 0 serial keys, 0 xpath keys
--   Contents:
--    18 attributes (0 in-place document keys), 0 elements (0 in-place document keys), 2 simple contents (0 in-place document keys, 1 as attribute, 0 as conditional attribute)
--   Wild cards:
--    0 any elements, 0 any attributes
--   Constraints:
--    0 unique constraints from xs:key, 0 unique constraints from xs:unique, 0 foreign key constraints from xs:keyref
--

--
-- JUnit test result schema for the Apache Ant JUnit and JUnitReport tasks Copyright © 2011, Windy Road Technology Pty. Limited The Apache Ant JUnit XML Schema is distributed under the terms of the Apache License Version 2.0 http://www.apache.org/licenses/ Permission to waive conditions of this license may be requested from Windy Road Support (http://windyroad.org/support).
--

DROP TABLE IF EXISTS testsuites CASCADE;
DROP TABLE IF EXISTS iso8601_datetime_pattern CASCADE;
DROP TABLE IF EXISTS property CASCADE;
DROP TABLE IF EXISTS skipped CASCADE;
DROP TABLE IF EXISTS pre_string CASCADE;
DROP TABLE IF EXISTS properties CASCADE;
DROP TABLE IF EXISTS error CASCADE;
DROP TABLE IF EXISTS failure CASCADE;
DROP TABLE IF EXISTS testcase CASCADE;
DROP TABLE IF EXISTS testsuite CASCADE;

--
-- Contains the results of exexuting a testsuite
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: root child, content: true, list: true, bridge: false, virtual: false, name collision: true
--
CREATE TABLE testsuite (
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 ) ,
-- ATTRIBUTE
-- must not be NULL, but dismissed due to name collision
	package TEXT ,
-- ATTRIBUTE
-- must not be NULL, but dismissed due to name collision
	id INTEGER ,
-- NESTED KEY : properties ( properties_id )
	properties_id BIGINT CHECK ( properties_id >= 0 ) ,
-- NESTED KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- NESTED KEY : system_out ( system_out_id, DELEGATED TO pre_string_id )
	system_out_id BIGINT CHECK ( system_out_id >= 0 ) ,
-- NESTED KEY : system_err ( system_err_id, DELEGATED TO pre_string_id )
	system_err_id BIGINT CHECK ( system_err_id >= 0 ) ,
-- ATTRIBUTE
-- must not be NULL, but dismissed due to name collision
-- xs:restriction/xs:minLength="1"
	name TEXT ,
-- NESTED KEY AS ATTRIBUTE : timestamp ( timestamp_id, DELEGATED TO iso8601_datetime_pattern_id )
	timestamp_id BIGINT CHECK ( timestamp_id >= 0 ) ,
-- ATTRIBUTE
-- must not be NULL, but dismissed due to name collision
-- xs:restriction/xs:minLength="1"
	hostname TEXT ,
-- ATTRIBUTE
-- must not be NULL, but dismissed due to name collision
	tests INTEGER ,
-- ATTRIBUTE
-- must not be NULL, but dismissed due to name collision
	failures INTEGER ,
-- ATTRIBUTE
-- must not be NULL, but dismissed due to name collision
	errors INTEGER ,
-- ATTRIBUTE
	skipped INTEGER ,
-- ATTRIBUTE
-- must not be NULL, but dismissed due to name collision
	time DECIMAL
);

--
-- Properties (e.g., environment settings) set during test execution
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin child, content: false, list: true, bridge: true, virtual: false
--
CREATE TABLE properties (
	properties_id BIGINT CHECK ( properties_id >= 0 ) ,
-- FOREIGN KEY : testsuite ( testsuite_id )
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 ) ,
-- NESTED KEY : property ( property_id )
	property_id BIGINT CHECK ( property_id >= 0 )
);

--
-- Indicates that the test errored. An errored test is one that had an unanticipated problem. e.g., an unchecked throwable; or a problem with the implementation of the test. Contains as a text node relevant data for the error, e.g., a stack trace
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin child, content: true, list: false, bridge: true, virtual: false
--
CREATE TABLE error (
-- PRIMARY KEY
	error_id BIGINT CHECK ( error_id >= 0 ) PRIMARY KEY ,
-- NESTED KEY : pre_string ( pre_string_id )
	pre_string_id BIGINT CHECK ( pre_string_id >= 0 ) ,
-- ATTRIBUTE
	message TEXT ,
-- ATTRIBUTE
	type TEXT NOT NULL
);

--
-- Indicates that the test failed. A failure is a test which the code has explicitly failed by using the mechanisms for that purpose. e.g., via an assertEquals. Contains as a text node relevant data for the failure, e.g., a stack trace
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin child, content: true, list: false, bridge: true, virtual: false
--
CREATE TABLE failure (
-- PRIMARY KEY
	failure_id BIGINT CHECK ( failure_id >= 0 ) PRIMARY KEY ,
-- NESTED KEY : pre_string ( pre_string_id )
	pre_string_id BIGINT CHECK ( pre_string_id >= 0 ) ,
-- ATTRIBUTE
	message TEXT ,
-- ATTRIBUTE
	type TEXT NOT NULL
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE testcase (
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- FOREIGN KEY : testsuite ( testsuite_id )
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 ) ,
-- NESTED KEY : skipped ( skipped_id )
	skipped_id BIGINT CHECK ( skipped_id >= 0 ) ,
-- NESTED KEY : error ( error_id, DELEGATED TO pre_string_id )
	error_id BIGINT CHECK ( error_id >= 0 ) ,
-- NESTED KEY : failure ( failure_id, DELEGATED TO pre_string_id )
	failure_id BIGINT CHECK ( failure_id >= 0 ) ,
-- ATTRIBUTE
	name TEXT NOT NULL ,
-- ATTRIBUTE
	classname TEXT NOT NULL ,
-- ATTRIBUTE
	time DECIMAL NOT NULL
);

--
-- Contains an aggregation of testsuite results
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: root, content: false, list: true, bridge: true, virtual: false
--
CREATE TABLE testsuites (
	testsuites_id BIGINT CHECK ( testsuites_id >= 0 ) ,
-- NESTED KEY : testsuite ( testsuite_id )
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 )
);

--
-- when the test was executed. Timezone may not be specified.
-- canonical name: ISO8601_DATETIME_PATTERN
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin root, content: true, list: false, bridge: false, virtual: true
--
CREATE TABLE iso8601_datetime_pattern (
-- PRIMARY KEY
	iso8601_datetime_pattern_id BIGINT CHECK ( iso8601_datetime_pattern_id >= 0 ) PRIMARY KEY ,
-- SIMPLE CONTENT AS ATTRIBUTE, ATTRIBUTE NODE: timestamp
-- xs:restriction/xs:pattern="[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}"
	content TIMESTAMP WITH TIME ZONE ,
-- FOREIGN KEY : timestamp ( timestamp_id )
	timestamp_id BIGINT CHECK ( timestamp_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE property (
	property_id BIGINT CHECK ( property_id >= 0 ) ,
-- FOREIGN KEY : properties ( properties_id )
	properties_id BIGINT CHECK ( properties_id >= 0 ) ,
-- ATTRIBUTE
-- xs:restriction/xs:minLength="1"
	name TEXT NOT NULL ,
-- ATTRIBUTE
	value TEXT NOT NULL
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE skipped (
-- PRIMARY KEY
	skipped_id BIGINT CHECK ( skipped_id >= 0 ) PRIMARY KEY ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 )
);

--
-- No annotation is available
-- canonical name: pre-string
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin root, content: true, list: false, bridge: false, virtual: true
--
CREATE TABLE pre_string (
-- PRIMARY KEY
	pre_string_id BIGINT CHECK ( pre_string_id >= 0 ) PRIMARY KEY ,
-- FOREIGN KEY : error ( error_id )
	error_id BIGINT CHECK ( error_id >= 0 ) CONSTRAINT FK_pre_string_error REFERENCES error ( error_id ) ON DELETE CASCADE ,
-- FOREIGN KEY : failure ( failure_id )
	failure_id BIGINT CHECK ( failure_id >= 0 ) CONSTRAINT FK_pre_string_failure REFERENCES failure ( failure_id ) ON DELETE CASCADE ,
-- SIMPLE CONTENT
	content TEXT
);

--
-- Data that was written to standard out while the test was executed
-- canonical name: system-out
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin child (view), content: false, list: false, bridge: true, virtual: false
--
CREATE OR REPLACE VIEW system_out AS
SELECT
	system_out_id ,
-- NESTED KEY : pre_string ( pre_string_id )
	system_out_id AS pre_string_id
FROM testsuite WHERE system_out_id IS NOT NULL;

--
-- Data that was written to standard error while the test was executed
-- canonical name: system-err
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin child (view), content: false, list: false, bridge: true, virtual: false
--
CREATE OR REPLACE VIEW system_err AS
SELECT
	system_err_id ,
-- NESTED KEY : pre_string ( pre_string_id )
	system_err_id AS pre_string_id
FROM testsuite WHERE system_err_id IS NOT NULL;

--
-- when the test was executed. Timezone may not be specified.
-- xmlns: no namespace, schema location: JUnit.xsd
-- type: admin child (view), content: false, list: false, bridge: true, virtual: false
--
CREATE OR REPLACE VIEW timestamp AS
SELECT
	timestamp_id ,
-- NESTED KEY AS ATTRIBUTE : iso8601_datetime_pattern ( iso8601_datetime_pattern_id )
	timestamp_id AS iso8601_datetime_pattern_id
FROM testsuite WHERE timestamp_id IS NOT NULL;

