# reportcard-client

## Requirements

Building the API client library requires [Maven](https://maven.apache.org/) to be installed.

## Installation

To install the API client library to your local Maven repository, simply execute:

```shell
mvn install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn deploy
```

Refer to the [official documentation](https://maven.apache.org/plugins/maven-deploy-plugin/usage.html) for more information.

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
    <groupId>com.ericdriggs</groupId>
    <artifactId>reportcard-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

### Gradle users

Add this dependency to your project's build file:

```groovy
compile "com.ericdriggs:reportcard-client:0.0.1-SNAPSHOT"
```

### Others

At first generate the JAR by executing:

    mvn package

Then manually install the following JARs:

* target/reportcard-client-0.0.1-SNAPSHOT.jar
* target/lib/*.jar

## Getting Started

Please follow the [installation](#installation) instruction and execute the following Java code:

```java

import com.ericdriggs.reportcard.client.invoker.*;
import com.ericdriggs.reportcard.client.invoker.auth.*;
import com.ericdriggs.reportcard.client.api.*;
import com.ericdriggs.reportcard.client.api.BasicErrorControllerApi;

import java.io.File;
import java.util.*;

public class BasicErrorControllerApiExample {

    public static void main(String[] args) {
        
        BasicErrorControllerApi apiInstance = new BasicErrorControllerApi();
        try {
            Map<String, Object> result = apiInstance.errorUsingDELETE();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling BasicErrorControllerApi#errorUsingDELETE");
            e.printStackTrace();
        }
    }
}

```

## Documentation for API Endpoints

All URIs are relative to *https://localhost:8080*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*BasicErrorControllerApi* | [**errorUsingDELETE**](docs/BasicErrorControllerApi.md#errorUsingDELETE) | **DELETE** /error | error
*BasicErrorControllerApi* | [**errorUsingGET**](docs/BasicErrorControllerApi.md#errorUsingGET) | **GET** /error | error
*BasicErrorControllerApi* | [**errorUsingHEAD**](docs/BasicErrorControllerApi.md#errorUsingHEAD) | **HEAD** /error | error
*BasicErrorControllerApi* | [**errorUsingOPTIONS**](docs/BasicErrorControllerApi.md#errorUsingOPTIONS) | **OPTIONS** /error | error
*BasicErrorControllerApi* | [**errorUsingPATCH**](docs/BasicErrorControllerApi.md#errorUsingPATCH) | **PATCH** /error | error
*BasicErrorControllerApi* | [**errorUsingPOST**](docs/BasicErrorControllerApi.md#errorUsingPOST) | **POST** /error | error
*BasicErrorControllerApi* | [**errorUsingPUT**](docs/BasicErrorControllerApi.md#errorUsingPUT) | **PUT** /error | error
*JsonControllerApi* | [**getAppBranchUsingGET**](docs/JsonControllerApi.md#getAppBranchUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch} | getAppBranch
*JsonControllerApi* | [**getAppBranchUsingGET1**](docs/JsonControllerApi.md#getAppBranchUsingGET1) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app} | getAppBranch
*JsonControllerApi* | [**getAppUsingGET**](docs/JsonControllerApi.md#getAppUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app} | getApp
*JsonControllerApi* | [**getAppsUsingGET**](docs/JsonControllerApi.md#getAppsUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps | getApps
*JsonControllerApi* | [**getBranchUsingGET**](docs/JsonControllerApi.md#getBranchUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch} | getBranch
*JsonControllerApi* | [**getBranchesUsingGET**](docs/JsonControllerApi.md#getBranchesUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/branches | getBranches
*JsonControllerApi* | [**getBuildUsingGET**](docs/JsonControllerApi.md#getBuildUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds/{buildOrdinal} | getBuild
*JsonControllerApi* | [**getBuildUsingGET1**](docs/JsonControllerApi.md#getBuildUsingGET1) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/builds/{buildOrdinal} | getBuild
*JsonControllerApi* | [**getBuildsUsingGET**](docs/JsonControllerApi.md#getBuildsUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds | getBuilds
*JsonControllerApi* | [**getOrgUsingGET**](docs/JsonControllerApi.md#getOrgUsingGET) | **GET** /api/orgs/{org} | getOrg
*JsonControllerApi* | [**getOrgsUsingGET**](docs/JsonControllerApi.md#getOrgsUsingGET) | **GET** /api/orgs | getOrgs
*JsonControllerApi* | [**getRepoUsingGET**](docs/JsonControllerApi.md#getRepoUsingGET) | **GET** /api/orgs/{org}/repos/{repo} | getRepo
*JsonControllerApi* | [**getReposUsingGET**](docs/JsonControllerApi.md#getReposUsingGET) | **GET** /api/orgs/{org}/repos | getRepos
*JsonControllerApi* | [**getStageUsingGET**](docs/JsonControllerApi.md#getStageUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds/{buildOrdinal}/stages/{stage} | getStage
*JsonControllerApi* | [**getStageUsingGET1**](docs/JsonControllerApi.md#getStageUsingGET1) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages/{stage}/builds/{buildOrdinal} | getStage
*JsonControllerApi* | [**getStageUsingGET2**](docs/JsonControllerApi.md#getStageUsingGET2) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/builds/{buildOrdinal}/stages/{stage} | getStage
*JsonControllerApi* | [**getStageUsingGET3**](docs/JsonControllerApi.md#getStageUsingGET3) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages/{stage}/builds/{buildOrdinal} | getStage
*JsonControllerApi* | [**getStageUsingGET4**](docs/JsonControllerApi.md#getStageUsingGET4) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages/{stage} | getStage
*JsonControllerApi* | [**getStageUsingGET5**](docs/JsonControllerApi.md#getStageUsingGET5) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages/{stage} | getStage
*JsonControllerApi* | [**getStagesUsingGET**](docs/JsonControllerApi.md#getStagesUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages | getStages
*JsonControllerApi* | [**getStagesUsingGET1**](docs/JsonControllerApi.md#getStagesUsingGET1) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages | getStages
*OperationHandlerApi* | [**handleUsingGET**](docs/OperationHandlerApi.md#handleUsingGET) | **GET** /actuator/health/** | handle
*OperationHandlerApi* | [**handleUsingGET1**](docs/OperationHandlerApi.md#handleUsingGET1) | **GET** /actuator/health | handle
*OperationHandlerApi* | [**handleUsingGET2**](docs/OperationHandlerApi.md#handleUsingGET2) | **GET** /actuator/info | handle
*ReportControllerApi* | [**postXmlJunitUsingPOST**](docs/ReportControllerApi.md#postXmlJunitUsingPOST) | **POST** /v1/xml/junit | postXmlJunit
*ReportControllerApi* | [**postXmlSurefireUsingPOST**](docs/ReportControllerApi.md#postXmlSurefireUsingPOST) | **POST** /v1/xml/surefire | postXmlSurefire
*ReportControllerApi* | [**postXmlUsingPOST**](docs/ReportControllerApi.md#postXmlUsingPOST) | **POST** /v1/xml | postXml
*WebMvcLinksHandlerApi* | [**linksUsingGET**](docs/WebMvcLinksHandlerApi.md#linksUsingGET) | **GET** /actuator | links


## Documentation for Models

 - [App](docs/App.md)
 - [AppBranch](docs/AppBranch.md)
 - [Branch](docs/Branch.md)
 - [Build](docs/Build.md)
 - [Link](docs/Link.md)
 - [MapstringLink](docs/MapstringLink.md)
 - [ModelAndView](docs/ModelAndView.md)
 - [Org](docs/Org.md)
 - [Repo](docs/Repo.md)
 - [ReportMetaData](docs/ReportMetaData.md)
 - [Stage](docs/Stage.md)
 - [TestCase](docs/TestCase.md)
 - [TestResult](docs/TestResult.md)
 - [TestSuite](docs/TestSuite.md)
 - [View](docs/View.md)


## Documentation for Authorization

All endpoints do not require authorization.
Authentication schemes defined for the API:

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author



