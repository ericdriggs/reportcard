# Reportcard
Test XML reporting publishing, aggregation and querying

TODO PROTOTYPE:

* implement reportcard-scanner with reportcard-scanner.properties file format and read env

* cross-compile reportcard-scanner with jdk8 and jdk11 - https://medium.com/uptake-tech/migrating-to-java-11-while-maintaining-a-java-8-client-library-f618a3ca6499
* implement JSON GET endpoints for hierarchy after POST
* implement UI GET endpoints for hierarchy after POST

TODO MVP:
* secure secrets (env vars ok, expiring tokens better)
* db migration tool (flyway)
* tests for uniqueness constraints all tables
