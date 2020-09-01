# Reportcard
Test XML reporting publishing, aggregation and querying

TODO PROTOTYPE:
* make build external identifier unique with constraint and default to uuid if not provided
* change build stage path query to accept external identifier -- build ordinal is internal only
* implement endpoint for publishing suefire xml repots
* implement reportcard-scanner with reportcard-scanner.properties file format and read env
* cross-compile reportcard-scanner with jdk8 and jdk11 - https://medium.com/uptake-tech/migrating-to-java-11-while-maintaining-a-java-8-client-library-f618a3ca6499
* implemnent JSON GET endpoints for hierarchy after POST
* implement UI GET endpoints for hierarchy after POST
* add dml sql for test setup with data for all tables

TODO MVP:
* secure secrets
* db migration tool (flyway)
* tests for uniqueness constraints all tables
