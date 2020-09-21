--
-- PostgreSQL DDL generated from testng-1.0.xsd using xsd2pgschema
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
--  Generated 40 tables (181 fields), 0 views (0 fields), 0 attr groups, 0 model groups in total
--   Namespaces:
--    http://www.w3.org/2001/XMLSchema (xs)
--   Schema locations:
--    testng-1.0.xsd
--   Table types:
--    1 root, 38 root children, 1 admin roots, 0 admin children
--   System keys:
--    40 primary keys (1 unique constraints), 54 foreign keys, 71 nested keys (29 as attribute, 0 as attribute group)
--   User keys:
--    0 document keys, 0 serial keys, 0 xpath keys
--   Contents:
--    15 attributes (0 in-place document keys), 0 elements (0 in-place document keys), 0 simple contents (0 in-place document keys, 0 as attribute, 0 as conditional attribute)
--   Wild cards:
--    1 any elements, 0 any attributes
--   Constraints:
--    0 unique constraints from xs:key, 0 unique constraints from xs:unique, 0 foreign key constraints from xs:keyref
--

DROP TABLE IF EXISTS "any" CASCADE;
DROP TABLE IF EXISTS name CASCADE;
DROP TABLE IF EXISTS description CASCADE;
DROP TABLE IF EXISTS invocation_numbers CASCADE;
DROP TABLE IF EXISTS run CASCADE;
DROP TABLE IF EXISTS depends_on CASCADE;
DROP TABLE IF EXISTS dependencies CASCADE;
DROP TABLE IF EXISTS class_name CASCADE;
DROP TABLE IF EXISTS listeners CASCADE;
DROP TABLE IF EXISTS priority CASCADE;
DROP TABLE IF EXISTS language CASCADE;
DROP TABLE IF EXISTS method_selector CASCADE;
DROP TABLE IF EXISTS value CASCADE;
DROP TABLE IF EXISTS methods CASCADE;
DROP TABLE IF EXISTS "verbose" CASCADE;
DROP TABLE IF EXISTS thread_count CASCADE;
DROP TABLE IF EXISTS annotations CASCADE;
DROP TABLE IF EXISTS time_out CASCADE;
DROP TABLE IF EXISTS path CASCADE;
DROP TABLE IF EXISTS suite_files CASCADE;
DROP TABLE IF EXISTS parent_module CASCADE;
DROP TABLE IF EXISTS data_provider_thread_count CASCADE;
DROP TABLE IF EXISTS object_factory CASCADE;
DROP TABLE IF EXISTS define CASCADE;
DROP TABLE IF EXISTS package CASCADE;
DROP TABLE IF EXISTS method_selectors CASCADE;
DROP TABLE IF EXISTS exclude CASCADE;
DROP TABLE IF EXISTS listener CASCADE;
DROP TABLE IF EXISTS packages CASCADE;
DROP TABLE IF EXISTS script CASCADE;
DROP TABLE IF EXISTS class CASCADE;
DROP TABLE IF EXISTS suite_file CASCADE;
DROP TABLE IF EXISTS "group" CASCADE;
DROP TABLE IF EXISTS selector_class CASCADE;
DROP TABLE IF EXISTS parameter CASCADE;
DROP TABLE IF EXISTS classes CASCADE;
DROP TABLE IF EXISTS include CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS test CASCADE;
DROP TABLE IF EXISTS suite CASCADE;

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root, content: true, list: true, bridge: false, virtual: false
--
DROP TYPE IF EXISTS ENUM_suite_junit CASCADE;
CREATE TYPE ENUM_suite_junit AS ENUM ( 'true', 'false' );
DROP TYPE IF EXISTS ENUM_suite_parallel CASCADE;
CREATE TYPE ENUM_suite_parallel AS ENUM ( 'false', 'true', 'none', 'methods', 'tests', 'classes', 'instances' );
DROP TYPE IF EXISTS ENUM_suite_guice_stage CASCADE;
CREATE TYPE ENUM_suite_guice_stage AS ENUM ( 'DEVELOPMENT', 'PRODUCTION', 'TOOL' );
DROP TYPE IF EXISTS ENUM_suite_configfailurepolicy CASCADE;
CREATE TYPE ENUM_suite_configfailurepolicy AS ENUM ( 'skip', 'continue' );
DROP TYPE IF EXISTS ENUM_suite_skipfailedinvocationcounts CASCADE;
CREATE TYPE ENUM_suite_skipfailedinvocationcounts AS ENUM ( 'true', 'false' );
DROP TYPE IF EXISTS ENUM_suite_group_by_instances CASCADE;
CREATE TYPE ENUM_suite_group_by_instances AS ENUM ( 'true', 'false' );
DROP TYPE IF EXISTS ENUM_suite_preserve_order CASCADE;
CREATE TYPE ENUM_suite_preserve_order AS ENUM ( 'true', 'false' );
DROP TYPE IF EXISTS ENUM_suite_allow_return_values CASCADE;
CREATE TYPE ENUM_suite_allow_return_values AS ENUM ( 'true', 'false' );
CREATE TABLE suite (
	suite_id BIGINT CHECK ( suite_id >= 0 ) ,
-- NESTED KEY : groups ( groups_id )
	groups_id BIGINT CHECK ( groups_id >= 0 ) ,
-- NESTED KEY : listeners ( listeners_id )
	listeners_id BIGINT CHECK ( listeners_id >= 0 ) ,
-- NESTED KEY : packages ( packages_id )
	packages_id BIGINT CHECK ( packages_id >= 0 ) ,
-- NESTED KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 ) ,
-- NESTED KEY : parameter ( parameter_id )
	parameter_id BIGINT CHECK ( parameter_id >= 0 ) ,
-- NESTED KEY : method_selectors ( method_selectors_id )
	method_selectors_id BIGINT CHECK ( method_selectors_id >= 0 ) ,
-- NESTED KEY : suite_files ( suite_files_id )
	suite_files_id BIGINT CHECK ( suite_files_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 ) ,
-- ATTRIBUTE
-- @default="false"
	junit ENUM_suite_junit ,
-- NESTED KEY AS ATTRIBUTE : "verbose" ( verbose_id )
	verbose_id BIGINT CHECK ( verbose_id >= 0 ) ,
-- ATTRIBUTE
-- @default="none"
	parallel ENUM_suite_parallel ,
-- NESTED KEY AS ATTRIBUTE : parent_module ( parent_module_id )
	parent_module_id BIGINT CHECK ( parent_module_id >= 0 ) ,
-- ATTRIBUTE
-- @default="DEVELOPMENT"
	guice_stage ENUM_suite_guice_stage ,
-- ATTRIBUTE
-- @default="skip"
	configfailurepolicy ENUM_suite_configfailurepolicy ,
-- NESTED KEY AS ATTRIBUTE : thread_count ( thread_count_id )
	thread_count_id BIGINT CHECK ( thread_count_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : annotations ( annotations_id )
	annotations_id BIGINT CHECK ( annotations_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : time_out ( time_out_id )
	time_out_id BIGINT CHECK ( time_out_id >= 0 ) ,
-- ATTRIBUTE
-- @default="false"
	skipfailedinvocationcounts ENUM_suite_skipfailedinvocationcounts ,
-- NESTED KEY AS ATTRIBUTE : data_provider_thread_count ( data_provider_thread_count_id )
	data_provider_thread_count_id BIGINT CHECK ( data_provider_thread_count_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : object_factory ( object_factory_id )
	object_factory_id BIGINT CHECK ( object_factory_id >= 0 ) ,
-- ATTRIBUTE
-- @default="false"
	group_by_instances ENUM_suite_group_by_instances ,
-- ATTRIBUTE
-- @default="true"
	preserve_order ENUM_suite_preserve_order ,
-- ATTRIBUTE
-- @default="false"
	allow_return_values ENUM_suite_allow_return_values
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: true, list: true, bridge: false, virtual: false
--
DROP TYPE IF EXISTS ENUM_test_junit CASCADE;
CREATE TYPE ENUM_test_junit AS ENUM ( 'true', 'false' );
DROP TYPE IF EXISTS ENUM_test_parallel CASCADE;
CREATE TYPE ENUM_test_parallel AS ENUM ( 'false', 'true', 'none', 'methods', 'tests', 'classes', 'instances' );
DROP TYPE IF EXISTS ENUM_test_enabled CASCADE;
CREATE TYPE ENUM_test_enabled AS ENUM ( 'true', 'false' );
DROP TYPE IF EXISTS ENUM_test_skipfailedinvocationcounts CASCADE;
CREATE TYPE ENUM_test_skipfailedinvocationcounts AS ENUM ( 'true', 'false' );
DROP TYPE IF EXISTS ENUM_test_preserve_order CASCADE;
CREATE TYPE ENUM_test_preserve_order AS ENUM ( 'true', 'false' );
DROP TYPE IF EXISTS ENUM_test_group_by_instances CASCADE;
CREATE TYPE ENUM_test_group_by_instances AS ENUM ( 'true', 'false' );
DROP TYPE IF EXISTS ENUM_test_allow_return_values CASCADE;
CREATE TYPE ENUM_test_allow_return_values AS ENUM ( 'true', 'false' );
CREATE TABLE test (
	test_id BIGINT CHECK ( test_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 ) ,
-- NESTED KEY : method_selectors ( method_selectors_id )
	method_selectors_id BIGINT CHECK ( method_selectors_id >= 0 ) ,
-- NESTED KEY : parameter ( parameter_id )
	parameter_id BIGINT CHECK ( parameter_id >= 0 ) ,
-- NESTED KEY : groups ( groups_id )
	groups_id BIGINT CHECK ( groups_id >= 0 ) ,
-- NESTED KEY : packages ( packages_id )
	packages_id BIGINT CHECK ( packages_id >= 0 ) ,
-- NESTED KEY : classes ( classes_id )
	classes_id BIGINT CHECK ( classes_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 ) ,
-- ATTRIBUTE
-- @default="false"
	junit ENUM_test_junit ,
-- NESTED KEY AS ATTRIBUTE : "verbose" ( verbose_id )
	verbose_id BIGINT CHECK ( verbose_id >= 0 ) ,
-- ATTRIBUTE
	parallel ENUM_test_parallel ,
-- NESTED KEY AS ATTRIBUTE : thread_count ( thread_count_id )
	thread_count_id BIGINT CHECK ( thread_count_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : annotations ( annotations_id )
	annotations_id BIGINT CHECK ( annotations_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : time_out ( time_out_id )
	time_out_id BIGINT CHECK ( time_out_id >= 0 ) ,
-- ATTRIBUTE
	enabled ENUM_test_enabled ,
-- ATTRIBUTE
-- @default="false"
	skipfailedinvocationcounts ENUM_test_skipfailedinvocationcounts ,
-- ATTRIBUTE
-- @default="true"
	preserve_order ENUM_test_preserve_order ,
-- ATTRIBUTE
-- @default="false"
	group_by_instances ENUM_test_group_by_instances ,
-- ATTRIBUTE
-- @default="false"
	allow_return_values ENUM_test_allow_return_values
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE include (
	include_id BIGINT CHECK ( include_id >= 0 ) ,
-- NESTED KEY : "any" ( any_id )
	any_id BIGINT CHECK ( any_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : description ( description_id )
	description_id BIGINT CHECK ( description_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : invocation_numbers ( invocation_numbers_id )
	invocation_numbers_id BIGINT CHECK ( invocation_numbers_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE groups (
	groups_id BIGINT CHECK ( groups_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 ) ,
-- NESTED KEY : define ( define_id )
	define_id BIGINT CHECK ( define_id >= 0 ) ,
-- NESTED KEY : run ( run_id )
	run_id BIGINT CHECK ( run_id >= 0 ) ,
-- NESTED KEY : dependencies ( dependencies_id )
	dependencies_id BIGINT CHECK ( dependencies_id >= 0 ) ,
-- FOREIGN KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE "group" (
	group_id BIGINT CHECK ( group_id >= 0 ) ,
-- NESTED KEY : "any" ( any_id )
	any_id BIGINT CHECK ( any_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : depends_on ( depends_on_id )
	depends_on_id BIGINT CHECK ( depends_on_id >= 0 )
);

--
-- No annotation is available
-- canonical name: selector-class
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE selector_class (
	selector_class_id BIGINT CHECK ( selector_class_id >= 0 ) ,
-- NESTED KEY : "any" ( any_id )
	any_id BIGINT CHECK ( any_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : priority ( priority_id )
	priority_id BIGINT CHECK ( priority_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE parameter (
	parameter_id BIGINT CHECK ( parameter_id >= 0 ) ,
-- NESTED KEY : "any" ( any_id )
	any_id BIGINT CHECK ( any_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : value ( value_id )
	value_id BIGINT CHECK ( value_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE classes (
	classes_id BIGINT CHECK ( classes_id >= 0 ) ,
-- FOREIGN KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 ) ,
-- NESTED KEY : class ( class_id )
	class_id BIGINT CHECK ( class_id >= 0 ) ,
-- NESTED KEY : parameter ( parameter_id )
	parameter_id BIGINT CHECK ( parameter_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE exclude (
	exclude_id BIGINT CHECK ( exclude_id >= 0 ) ,
-- NESTED KEY : "any" ( any_id )
	any_id BIGINT CHECK ( any_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE listener (
	listener_id BIGINT CHECK ( listener_id >= 0 ) ,
-- NESTED KEY : "any" ( any_id )
	any_id BIGINT CHECK ( any_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : class_name ( class_name_id )
	class_name_id BIGINT CHECK ( class_name_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: true, virtual: false
--
CREATE TABLE packages (
	packages_id BIGINT CHECK ( packages_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 ) ,
-- NESTED KEY : package ( package_id )
	package_id BIGINT CHECK ( package_id >= 0 ) ,
-- FOREIGN KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE script (
	script_id BIGINT CHECK ( script_id >= 0 ) ,
-- NESTED KEY : "any" ( any_id )
	any_id BIGINT CHECK ( any_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : language ( language_id )
	language_id BIGINT CHECK ( language_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE class (
	class_id BIGINT CHECK ( class_id >= 0 ) ,
-- FOREIGN KEY : classes ( classes_id )
	classes_id BIGINT CHECK ( classes_id >= 0 ) ,
-- NESTED KEY : methods ( methods_id )
	methods_id BIGINT CHECK ( methods_id >= 0 ) ,
-- NESTED KEY : parameter ( parameter_id )
	parameter_id BIGINT CHECK ( parameter_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 )
);

--
-- No annotation is available
-- canonical name: suite-file
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE suite_file (
	suite_file_id BIGINT CHECK ( suite_file_id >= 0 ) ,
-- NESTED KEY : "any" ( any_id )
	any_id BIGINT CHECK ( any_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : path ( path_id )
	path_id BIGINT CHECK ( path_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE define (
	define_id BIGINT CHECK ( define_id >= 0 ) ,
-- FOREIGN KEY : groups ( groups_id )
	groups_id BIGINT CHECK ( groups_id >= 0 ) ,
-- NESTED KEY : include ( include_id )
	include_id BIGINT CHECK ( include_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE package (
	package_id BIGINT CHECK ( package_id >= 0 ) ,
-- FOREIGN KEY : packages ( packages_id )
	packages_id BIGINT CHECK ( packages_id >= 0 ) ,
-- NESTED KEY : include ( include_id )
	include_id BIGINT CHECK ( include_id >= 0 ) ,
-- NESTED KEY : exclude ( exclude_id )
	exclude_id BIGINT CHECK ( exclude_id >= 0 ) ,
-- NESTED KEY AS ATTRIBUTE : name ( name_id )
	name_id BIGINT CHECK ( name_id >= 0 )
);

--
-- No annotation is available
-- canonical name: method-selectors
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: true, virtual: false
--
CREATE TABLE method_selectors (
	method_selectors_id BIGINT CHECK ( method_selectors_id >= 0 ) ,
-- FOREIGN KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 ) ,
-- NESTED KEY : method_selector ( method_selector_id )
	method_selector_id BIGINT CHECK ( method_selector_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: admin root, content: true, list: false, bridge: false, virtual: true
--
CREATE TABLE "any" (
	any_id BIGINT CHECK ( any_id >= 0 ) ,
-- FOREIGN KEY : include ( include_id )
	include_id BIGINT CHECK ( include_id >= 0 ) ,
-- FOREIGN KEY : exclude ( exclude_id )
	exclude_id BIGINT CHECK ( exclude_id >= 0 ) ,
-- FOREIGN KEY : "group" ( group_id )
	group_id BIGINT CHECK ( group_id >= 0 ) ,
-- FOREIGN KEY : listener ( listener_id )
	listener_id BIGINT CHECK ( listener_id >= 0 ) ,
-- FOREIGN KEY : selector_class ( selector_class_id )
	selector_class_id BIGINT CHECK ( selector_class_id >= 0 ) ,
-- FOREIGN KEY : script ( script_id )
	script_id BIGINT CHECK ( script_id >= 0 ) ,
-- FOREIGN KEY : parameter ( parameter_id )
	parameter_id BIGINT CHECK ( parameter_id >= 0 ) ,
-- FOREIGN KEY : suite_file ( suite_file_id )
	suite_file_id BIGINT CHECK ( suite_file_id >= 0 ) ,
-- ANY ELEMENT
	any_element XML
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE name (
	name_id BIGINT CHECK ( name_id >= 0 ) ,
-- FOREIGN KEY : include ( include_id )
	include_id BIGINT CHECK ( include_id >= 0 ) ,
-- FOREIGN KEY : define ( define_id )
	define_id BIGINT CHECK ( define_id >= 0 ) ,
-- FOREIGN KEY : exclude ( exclude_id )
	exclude_id BIGINT CHECK ( exclude_id >= 0 ) ,
-- FOREIGN KEY : "group" ( group_id )
	group_id BIGINT CHECK ( group_id >= 0 ) ,
-- FOREIGN KEY : package ( package_id )
	package_id BIGINT CHECK ( package_id >= 0 ) ,
-- FOREIGN KEY : selector_class ( selector_class_id )
	selector_class_id BIGINT CHECK ( selector_class_id >= 0 ) ,
-- FOREIGN KEY : parameter ( parameter_id )
	parameter_id BIGINT CHECK ( parameter_id >= 0 ) ,
-- FOREIGN KEY : class ( class_id )
	class_id BIGINT CHECK ( class_id >= 0 ) ,
-- FOREIGN KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE description (
	description_id BIGINT CHECK ( description_id >= 0 ) ,
-- FOREIGN KEY : include ( include_id )
	include_id BIGINT CHECK ( include_id >= 0 )
);

--
-- No annotation is available
-- canonical name: invocation-numbers
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE invocation_numbers (
	invocation_numbers_id BIGINT CHECK ( invocation_numbers_id >= 0 ) ,
-- FOREIGN KEY : include ( include_id )
	include_id BIGINT CHECK ( include_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE run (
	run_id BIGINT CHECK ( run_id >= 0 ) ,
-- FOREIGN KEY : groups ( groups_id )
	groups_id BIGINT CHECK ( groups_id >= 0 ) ,
-- NESTED KEY : include ( include_id )
	include_id BIGINT CHECK ( include_id >= 0 ) ,
-- NESTED KEY : exclude ( exclude_id )
	exclude_id BIGINT CHECK ( exclude_id >= 0 )
);

--
-- No annotation is available
-- canonical name: depends-on
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE depends_on (
	depends_on_id BIGINT CHECK ( depends_on_id >= 0 ) ,
-- FOREIGN KEY : "group" ( group_id )
	group_id BIGINT CHECK ( group_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: true, virtual: false
--
CREATE TABLE dependencies (
	dependencies_id BIGINT CHECK ( dependencies_id >= 0 ) ,
-- FOREIGN KEY : groups ( groups_id )
	groups_id BIGINT CHECK ( groups_id >= 0 ) ,
-- NESTED KEY : "group" ( group_id )
	group_id BIGINT CHECK ( group_id >= 0 )
);

--
-- No annotation is available
-- canonical name: class-name
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE class_name (
	class_name_id BIGINT CHECK ( class_name_id >= 0 ) ,
-- FOREIGN KEY : listener ( listener_id )
	listener_id BIGINT CHECK ( listener_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: true, virtual: false
--
CREATE TABLE listeners (
	listeners_id BIGINT CHECK ( listeners_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 ) ,
-- NESTED KEY : listener ( listener_id )
	listener_id BIGINT CHECK ( listener_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE priority (
	priority_id BIGINT CHECK ( priority_id >= 0 ) ,
-- FOREIGN KEY : selector_class ( selector_class_id )
	selector_class_id BIGINT CHECK ( selector_class_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE language (
-- PRIMARY KEY
	language_id BIGINT CHECK ( language_id >= 0 ) PRIMARY KEY ,
-- FOREIGN KEY : script ( script_id )
	script_id BIGINT CHECK ( script_id >= 0 )
);

--
-- No annotation is available
-- canonical name: method-selector
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE method_selector (
	method_selector_id BIGINT CHECK ( method_selector_id >= 0 ) ,
-- FOREIGN KEY : method_selectors ( method_selectors_id )
	method_selectors_id BIGINT CHECK ( method_selectors_id >= 0 ) ,
-- NESTED KEY : selector_class ( selector_class_id )
	selector_class_id BIGINT CHECK ( selector_class_id >= 0 ) ,
-- NESTED KEY : script ( script_id )
	script_id BIGINT CHECK ( script_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE value (
	value_id BIGINT CHECK ( value_id >= 0 ) ,
-- FOREIGN KEY : parameter ( parameter_id )
	parameter_id BIGINT CHECK ( parameter_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: false, virtual: false
--
CREATE TABLE methods (
	methods_id BIGINT CHECK ( methods_id >= 0 ) ,
-- FOREIGN KEY : class ( class_id )
	class_id BIGINT CHECK ( class_id >= 0 ) ,
-- NESTED KEY : include ( include_id )
	include_id BIGINT CHECK ( include_id >= 0 ) ,
-- NESTED KEY : exclude ( exclude_id )
	exclude_id BIGINT CHECK ( exclude_id >= 0 ) ,
-- NESTED KEY : parameter ( parameter_id )
	parameter_id BIGINT CHECK ( parameter_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE "verbose" (
	verbose_id BIGINT CHECK ( verbose_id >= 0 ) ,
-- FOREIGN KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 )
);

--
-- No annotation is available
-- canonical name: thread-count
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE thread_count (
	thread_count_id BIGINT CHECK ( thread_count_id >= 0 ) ,
-- FOREIGN KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE annotations (
	annotations_id BIGINT CHECK ( annotations_id >= 0 ) ,
-- FOREIGN KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 )
);

--
-- No annotation is available
-- canonical name: time-out
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE time_out (
	time_out_id BIGINT CHECK ( time_out_id >= 0 ) ,
-- FOREIGN KEY : test ( test_id )
	test_id BIGINT CHECK ( test_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 )
);

--
-- No annotation is available
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE path (
	path_id BIGINT CHECK ( path_id >= 0 ) ,
-- FOREIGN KEY : suite_file ( suite_file_id )
	suite_file_id BIGINT CHECK ( suite_file_id >= 0 )
);

--
-- No annotation is available
-- canonical name: suite-files
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: true, bridge: true, virtual: false
--
CREATE TABLE suite_files (
	suite_files_id BIGINT CHECK ( suite_files_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 ) ,
-- NESTED KEY : suite_file ( suite_file_id )
	suite_file_id BIGINT CHECK ( suite_file_id >= 0 )
);

--
-- No annotation is available
-- canonical name: parent-module
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE parent_module (
	parent_module_id BIGINT CHECK ( parent_module_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 )
);

--
-- No annotation is available
-- canonical name: data-provider-thread-count
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE data_provider_thread_count (
	data_provider_thread_count_id BIGINT CHECK ( data_provider_thread_count_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 )
);

--
-- No annotation is available
-- canonical name: object-factory
-- xmlns: no namespace, schema location: testng-1.0.xsd
-- type: root child, content: false, list: false, bridge: false, virtual: false
--
CREATE TABLE object_factory (
	object_factory_id BIGINT CHECK ( object_factory_id >= 0 ) ,
-- FOREIGN KEY : suite ( suite_id )
	suite_id BIGINT CHECK ( suite_id >= 0 )
);

