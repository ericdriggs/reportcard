# Reportcard
Test XML reporting publishing, aggregation and querying

TODO PROTOTYPE:

* implement endpoint for publishing surefire xml reports
* create constructor to map from List of surefire testsuite to model TestResult
    ~ review surefire data and add missing columns / tables to schema
* implement reportcard-scanner with reportcard-scanner.properties file format and read env

* cross-compile reportcard-scanner with jdk8 and jdk11 - https://medium.com/uptake-tech/migrating-to-java-11-while-maintaining-a-java-8-client-library-f618a3ca6499
* implement JSON GET endpoints for hierarchy after POST
* implement UI GET endpoints for hierarchy after POST
* add dml sql for test setup with data for all tables (still need testresult hierarchy)

TODO MVP:
* secure secrets
* db migration tool (flyway)
* tests for uniqueness constraints all tables
