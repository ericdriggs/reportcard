--
-- PostgreSQL DDL generated from surefire-test-report-3.0.xsd using xsd2pgschema
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
--  no name collision: true
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
--  Generated 12 tables (77 fields), 1 views (2 fields), 0 attr groups, 0 model groups in total
--   Namespaces:
--    http://www.w3.org/2001/XMLSchema (xs)
--   Schema locations:
--    surefire-test-report-3.0.xsd
--   Table types:
--    1 root, 11 root children, 1 admin roots, 0 admin children
--   System keys:
--    13 primary keys (1 unique constraints), 11 foreign keys, 13 nested keys (3 as attribute, 0 as attribute group)
--   User keys:
--    0 document keys, 0 serial keys, 0 xpath keys
--   Contents:
--    24 attributes (0 in-place document keys), 14 elements (0 in-place document keys), 4 simple contents (0 in-place document keys, 1 as attribute, 0 as conditional attribute)
--   Wild cards:
--    0 any elements, 0 any attributes
--   Constraints:
--    0 unique constraints from xs:key, 0 unique constraints from xs:unique, 0 foreign key constraints from xs:keyref
--

DROP TABLE IF EXISTS property CASCADE;
DROP TABLE IF EXISTS failure CASCADE;
DROP TABLE IF EXISTS rerunfailure CASCADE;
DROP TABLE IF EXISTS flakyfailure CASCADE;
DROP TABLE IF EXISTS skipped CASCADE;
DROP TABLE IF EXISTS error CASCADE;
DROP TABLE IF EXISTS rerunerror CASCADE;
DROP TABLE IF EXISTS flakyerror CASCADE;
DROP TABLE IF EXISTS surefire_time CASCADE;
DROP TABLE IF EXISTS properties CASCADE;
DROP TABLE IF EXISTS testcase CASCADE;
DROP TABLE IF EXISTS testsuite CASCADE;

--
-- No annotation is available
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root, content: true, list: true, bridge: false, virtual: false
--
CREATE TABLE testsuite (
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 ) ,
-- NESTED KEY : properties ( properties_id )
	properties_id BIGINT CHECK ( properties_id >= 0 ) ,
-- NESTED KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- ATTRIBUTE
	name TEXT NOT NULL ,
-- NESTED KEY AS ATTRIBUTE : time ( time_id, DELEGATED TO surefire_time_id )
	time_id BIGINT CHECK ( time_id >= 0 ) ,
-- ATTRIBUTE
	tests TEXT NOT NULL ,
-- ATTRIBUTE
	errors TEXT NOT NULL ,
-- ATTRIBUTE
	skipped TEXT NOT NULL ,
-- ATTRIBUTE
	failures TEXT NOT NULL ,
-- ATTRIBUTE
	"group" TEXT
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: true, list: true, bridge: false, virtual: false
--
CREATE TABLE testcase (
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- FOREIGN KEY : testsuite ( testsuite_id )
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 ) ,
-- NESTED KEY : failure ( failure_id )
	failure_id BIGINT CHECK ( failure_id >= 0 ) ,
-- NESTED KEY : rerunfailure ( rerunfailure_id )
	rerunfailure_id BIGINT CHECK ( rerunfailure_id >= 0 ) ,
-- NESTED KEY : flakyfailure ( flakyfailure_id )
	flakyfailure_id BIGINT CHECK ( flakyfailure_id >= 0 ) ,
-- NESTED KEY : skipped ( skipped_id )
	skipped_id BIGINT CHECK ( skipped_id >= 0 ) ,
-- NESTED KEY : error ( error_id )
	error_id BIGINT CHECK ( error_id >= 0 ) ,
-- NESTED KEY : rerunerror ( rerunerror_id )
	rerunerror_id BIGINT CHECK ( rerunerror_id >= 0 ) ,
-- NESTED KEY : flakyerror ( flakyerror_id )
	flakyerror_id BIGINT CHECK ( flakyerror_id >= 0 ) ,
	system_out TEXT ,
	system_err TEXT ,
-- ATTRIBUTE
	name TEXT NOT NULL ,
-- ATTRIBUTE
	classname TEXT ,
-- ATTRIBUTE
	"group" TEXT ,
-- NESTED KEY AS ATTRIBUTE : time ( time_id, DELEGATED TO surefire_time_id )
	time_id BIGINT CHECK ( time_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: false, list: true, bridge: true, virtual: false
--
CREATE TABLE properties (
	properties_id BIGINT CHECK ( properties_id >= 0 ) ,
-- FOREIGN KEY : testsuite ( testsuite_id )
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 ) ,
-- NESTED KEY : property ( property_id )
	property_id BIGINT CHECK ( property_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE property (
	property_id BIGINT CHECK ( property_id >= 0 ) ,
-- FOREIGN KEY : properties ( properties_id )
	properties_id BIGINT CHECK ( properties_id >= 0 ) ,
-- ATTRIBUTE
	name TEXT NOT NULL ,
-- ATTRIBUTE
	value TEXT NOT NULL
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE failure (
	failure_id BIGINT CHECK ( failure_id >= 0 ) ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- SIMPLE CONTENT
	content TEXT ,
-- ATTRIBUTE
	message TEXT ,
-- ATTRIBUTE
	type TEXT NOT NULL
);

--
-- No annotation is available
-- canonical name: rerunFailure
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE rerunfailure (
	rerunfailure_id BIGINT CHECK ( rerunfailure_id >= 0 ) ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
	stacktrace TEXT ,
	system_out TEXT ,
	system_err TEXT ,
-- ATTRIBUTE
	message TEXT ,
-- ATTRIBUTE
	type TEXT NOT NULL
);

--
-- No annotation is available
-- canonical name: flakyFailure
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE flakyfailure (
	flakyfailure_id BIGINT CHECK ( flakyfailure_id >= 0 ) ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
	stacktrace TEXT ,
	system_out TEXT ,
	system_err TEXT ,
-- ATTRIBUTE
	message TEXT ,
-- ATTRIBUTE
	type TEXT NOT NULL
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE skipped (
	skipped_id BIGINT CHECK ( skipped_id >= 0 ) ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- SIMPLE CONTENT
	content TEXT ,
-- ATTRIBUTE
	message TEXT
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE error (
	error_id BIGINT CHECK ( error_id >= 0 ) ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- SIMPLE CONTENT
	content TEXT ,
-- ATTRIBUTE
	message TEXT ,
-- ATTRIBUTE
	type TEXT NOT NULL
);

--
-- No annotation is available
-- canonical name: rerunError
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE rerunerror (
	rerunerror_id BIGINT CHECK ( rerunerror_id >= 0 ) ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
	stacktrace TEXT ,
	system_out TEXT ,
	system_err TEXT ,
-- ATTRIBUTE
	message TEXT ,
-- ATTRIBUTE
	type TEXT NOT NULL
);

--
-- No annotation is available
-- canonical name: flakyError
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE flakyerror (
	flakyerror_id BIGINT CHECK ( flakyerror_id >= 0 ) ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
	stacktrace TEXT ,
	system_out TEXT ,
	system_err TEXT ,
-- ATTRIBUTE
	message TEXT ,
-- ATTRIBUTE
	type TEXT NOT NULL
);

--
-- No annotation is available
-- canonical name: SUREFIRE_TIME
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: admin root, content: true, list: false, bridge: false, virtual: true
--
CREATE TABLE surefire_time (
-- PRIMARY KEY
	surefire_time_id BIGINT CHECK ( surefire_time_id >= 0 ) PRIMARY KEY ,
-- FOREIGN KEY : time ( time_id )
	time_id BIGINT CHECK ( time_id >= 0 ) ,
-- SIMPLE CONTENT AS ATTRIBUTE, ATTRIBUTE NODE: time
-- xs:restriction/xs:pattern="(([0-9]{0,3},)*[0-9]{3}|[0-9]{0,3})*(\.[0-9]{0,3})?"
	content TEXT
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: surefire-test-report-3.0.xsd
-- type: root child (view), content: false, list: false, bridge: true, virtual: false
--
CREATE OR REPLACE VIEW time AS
SELECT
	time_id ,
-- NESTED KEY AS ATTRIBUTE : surefire_time ( surefire_time_id )
	time_id AS surefire_time_id
FROM testcase WHERE time_id IS NOT NULL
UNION ALL
SELECT
	time_id ,
-- NESTED KEY AS ATTRIBUTE : surefire_time ( surefire_time_id )
	time_id AS surefire_time_id
FROM testsuite WHERE time_id IS NOT NULL;

