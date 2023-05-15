--
-- PostgreSQL DDL generated from jenkins-junit.xsd using xsd2pgschema
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
--  Generated 7 tables (54 fields), 0 views (0 fields), 0 attr groups, 0 model groups in total
--   Namespaces:
--    http://www.w3.org/2001/XMLSchema (xs)
--   Schema locations:
--    jenkins-junit.xsd
--   Table types:
--    1 root, 6 root children, 0 admin roots, 0 admin children
--   System keys:
--    7 primary keys (0 unique constraints), 6 foreign keys, 6 nested keys (0 as attribute, 0 as attribute group)
--   User keys:
--    0 document keys, 0 serial keys, 0 xpath keys
--   Contents:
--    28 attributes (0 in-place document keys), 5 elements (0 in-place document keys), 2 simple contents (0 in-place document keys, 0 as attribute, 0 as conditional attribute)
--   Wild cards:
--    0 any elements, 0 any attributes
--   Constraints:
--    0 unique constraints from xs:key, 0 unique constraints from xs:unique, 0 foreign key constraints from xs:keyref
--

DROP TABLE IF EXISTS property CASCADE;
DROP TABLE IF EXISTS error CASCADE;
DROP TABLE IF EXISTS failure CASCADE;
DROP TABLE IF EXISTS properties CASCADE;
DROP TABLE IF EXISTS testcase CASCADE;
DROP TABLE IF EXISTS testsuite CASCADE;
DROP TABLE IF EXISTS testsuites CASCADE;

--
-- No annotation is available
-- xmlns: no namespace, schema location: jenkins-junit.xsd
-- type: root, content: true, list: true, bridge: true, virtual: false
--
CREATE TABLE testsuites (
	testsuites_id BIGINT CHECK ( testsuites_id >= 0 ) ,
-- NESTED KEY : testsuite ( testsuite_id )
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 ) ,
-- ATTRIBUTE
	name TEXT ,
-- ATTRIBUTE
	time TEXT ,
-- ATTRIBUTE
	tests TEXT ,
-- ATTRIBUTE
	failures TEXT ,
-- ATTRIBUTE
	disabled TEXT ,
-- ATTRIBUTE
	errors TEXT
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: jenkins-junit.xsd
-- type: root child, content: true, list: true, bridge: false, virtual: false
--
CREATE TABLE testsuite (
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 ) ,
-- FOREIGN KEY : testsuites ( testsuites_id )
	testsuites_id BIGINT CHECK ( testsuites_id >= 0 ) ,
-- NESTED KEY : properties ( properties_id )
	properties_id BIGINT CHECK ( properties_id >= 0 ) ,
-- NESTED KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
	system_out TEXT ,
	system_err TEXT ,
-- ATTRIBUTE
	name TEXT NOT NULL ,
-- ATTRIBUTE
	tests TEXT NOT NULL ,
-- ATTRIBUTE
	failures TEXT ,
-- ATTRIBUTE
	errors TEXT ,
-- ATTRIBUTE
	time TEXT ,
-- ATTRIBUTE
	disabled TEXT ,
-- ATTRIBUTE
	skipped TEXT ,
-- ATTRIBUTE
	timestamp TEXT ,
-- ATTRIBUTE
	hostname TEXT ,
-- ATTRIBUTE
	id TEXT ,
-- ATTRIBUTE
	package TEXT
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: jenkins-junit.xsd
-- type: root child, content: true, list: true, bridge: false, virtual: false
--
CREATE TABLE testcase (
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- FOREIGN KEY : testsuite ( testsuite_id )
	testsuite_id BIGINT CHECK ( testsuite_id >= 0 ) ,
	skipped TEXT ,
-- NESTED KEY : error ( error_id )
	error_id BIGINT CHECK ( error_id >= 0 ) ,
-- NESTED KEY : failure ( failure_id )
	failure_id BIGINT CHECK ( failure_id >= 0 ) ,
	system_out TEXT ,
	system_err TEXT ,
-- ATTRIBUTE
	name TEXT NOT NULL ,
-- ATTRIBUTE
	assertions TEXT ,
-- ATTRIBUTE
	time TEXT ,
-- ATTRIBUTE
	classname TEXT ,
-- ATTRIBUTE
	status TEXT
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: jenkins-junit.xsd
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
-- xmlns: no namespace, schema location: jenkins-junit.xsd
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
-- xmlns: no namespace, schema location: jenkins-junit.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE error (
	error_id BIGINT CHECK ( error_id >= 0 ) ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- SIMPLE CONTENT
	content TEXT ,
-- ATTRIBUTE
	type TEXT ,
-- ATTRIBUTE
	message TEXT
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: jenkins-junit.xsd
-- type: root child, content: true, list: false, bridge: false, virtual: false
--
CREATE TABLE failure (
	failure_id BIGINT CHECK ( failure_id >= 0 ) ,
-- FOREIGN KEY : testcase ( testcase_id )
	testcase_id BIGINT CHECK ( testcase_id >= 0 ) ,
-- SIMPLE CONTENT
	content TEXT ,
-- ATTRIBUTE
	type TEXT ,
-- ATTRIBUTE
	message TEXT
);

