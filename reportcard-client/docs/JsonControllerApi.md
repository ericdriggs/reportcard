# JsonControllerApi

All URIs are relative to *https://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAppBranchUsingGET**](JsonControllerApi.md#getAppBranchUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch} | getAppBranch
[**getAppBranchUsingGET1**](JsonControllerApi.md#getAppBranchUsingGET1) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app} | getAppBranch
[**getAppUsingGET**](JsonControllerApi.md#getAppUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app} | getApp
[**getAppsUsingGET**](JsonControllerApi.md#getAppsUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps | getApps
[**getBranchUsingGET**](JsonControllerApi.md#getBranchUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch} | getBranch
[**getBranchesUsingGET**](JsonControllerApi.md#getBranchesUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/branches | getBranches
[**getBuildUsingGET**](JsonControllerApi.md#getBuildUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds/{buildOrdinal} | getBuild
[**getBuildUsingGET1**](JsonControllerApi.md#getBuildUsingGET1) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/builds/{buildOrdinal} | getBuild
[**getBuildsUsingGET**](JsonControllerApi.md#getBuildsUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds | getBuilds
[**getOrgUsingGET**](JsonControllerApi.md#getOrgUsingGET) | **GET** /api/orgs/{org} | getOrg
[**getOrgsUsingGET**](JsonControllerApi.md#getOrgsUsingGET) | **GET** /api/orgs | getOrgs
[**getRepoUsingGET**](JsonControllerApi.md#getRepoUsingGET) | **GET** /api/orgs/{org}/repos/{repo} | getRepo
[**getReposUsingGET**](JsonControllerApi.md#getReposUsingGET) | **GET** /api/orgs/{org}/repos | getRepos
[**getStageUsingGET**](JsonControllerApi.md#getStageUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds/{buildOrdinal}/stages/{stage} | getStage
[**getStageUsingGET1**](JsonControllerApi.md#getStageUsingGET1) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages/{stage}/builds/{buildOrdinal} | getStage
[**getStageUsingGET2**](JsonControllerApi.md#getStageUsingGET2) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/builds/{buildOrdinal}/stages/{stage} | getStage
[**getStageUsingGET3**](JsonControllerApi.md#getStageUsingGET3) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages/{stage}/builds/{buildOrdinal} | getStage
[**getStageUsingGET4**](JsonControllerApi.md#getStageUsingGET4) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages/{stage} | getStage
[**getStageUsingGET5**](JsonControllerApi.md#getStageUsingGET5) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages/{stage} | getStage
[**getStagesUsingGET**](JsonControllerApi.md#getStagesUsingGET) | **GET** /api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages | getStages
[**getStagesUsingGET1**](JsonControllerApi.md#getStagesUsingGET1) | **GET** /api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages | getStages


<a name="getAppBranchUsingGET"></a>
# **getAppBranchUsingGET**
> AppBranch getAppBranchUsingGET(app, branch, org, repo)

getAppBranch

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    AppBranch result = apiInstance.getAppBranchUsingGET(app, branch, org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getAppBranchUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**AppBranch**](AppBranch.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getAppBranchUsingGET1"></a>
# **getAppBranchUsingGET1**
> AppBranch getAppBranchUsingGET1(app, branch, org, repo)

getAppBranch

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    AppBranch result = apiInstance.getAppBranchUsingGET1(app, branch, org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getAppBranchUsingGET1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**AppBranch**](AppBranch.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getAppUsingGET"></a>
# **getAppUsingGET**
> App getAppUsingGET(app, org, repo)

getApp

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    App result = apiInstance.getAppUsingGET(app, org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getAppUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**App**](App.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getAppsUsingGET"></a>
# **getAppsUsingGET**
> List&lt;App&gt; getAppsUsingGET(org, repo)

getApps

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    List<App> result = apiInstance.getAppsUsingGET(org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getAppsUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**List&lt;App&gt;**](App.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getBranchUsingGET"></a>
# **getBranchUsingGET**
> Branch getBranchUsingGET(branch, org, repo)

getBranch

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String branch = "branch_example"; // String | branch
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    Branch result = apiInstance.getBranchUsingGET(branch, org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getBranchUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **branch** | **String**| branch |
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**Branch**](Branch.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getBranchesUsingGET"></a>
# **getBranchesUsingGET**
> List&lt;Branch&gt; getBranchesUsingGET(org, repo)

getBranches

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    List<Branch> result = apiInstance.getBranchesUsingGET(org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getBranchesUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**List&lt;Branch&gt;**](Branch.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getBuildUsingGET"></a>
# **getBuildUsingGET**
> Build getBuildUsingGET(app, branch, buildUniqueString, org, repo)

getBuild

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String buildUniqueString = "buildUniqueString_example"; // String | buildUniqueString
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    Build result = apiInstance.getBuildUsingGET(app, branch, buildUniqueString, org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getBuildUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **buildUniqueString** | **String**| buildUniqueString |
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**Build**](Build.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getBuildUsingGET1"></a>
# **getBuildUsingGET1**
> Build getBuildUsingGET1(app, branch, buildUniqueString, org, repo)

getBuild

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String buildUniqueString = "buildUniqueString_example"; // String | buildUniqueString
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    Build result = apiInstance.getBuildUsingGET1(app, branch, buildUniqueString, org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getBuildUsingGET1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **buildUniqueString** | **String**| buildUniqueString |
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**Build**](Build.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getBuildsUsingGET"></a>
# **getBuildsUsingGET**
> List&lt;Build&gt; getBuildsUsingGET(app, branch, org, repo)

getBuilds

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    List<Build> result = apiInstance.getBuildsUsingGET(app, branch, org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getBuildsUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**List&lt;Build&gt;**](Build.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getOrgUsingGET"></a>
# **getOrgUsingGET**
> Org getOrgUsingGET(org)

getOrg

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String org = "org_example"; // String | org
try {
    Org result = apiInstance.getOrgUsingGET(org);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getOrgUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **org** | **String**| org |

### Return type

[**Org**](Org.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getOrgsUsingGET"></a>
# **getOrgsUsingGET**
> List&lt;Org&gt; getOrgsUsingGET(org)

getOrgs

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String org = "org_example"; // String | org
try {
    List<Org> result = apiInstance.getOrgsUsingGET(org);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getOrgsUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **org** | **String**| org |

### Return type

[**List&lt;Org&gt;**](Org.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getRepoUsingGET"></a>
# **getRepoUsingGET**
> Repo getRepoUsingGET(org, repo)

getRepo

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    Repo result = apiInstance.getRepoUsingGET(org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getRepoUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**Repo**](Repo.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getReposUsingGET"></a>
# **getReposUsingGET**
> List&lt;Repo&gt; getReposUsingGET(org)

getRepos

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String org = "org_example"; // String | org
try {
    List<Repo> result = apiInstance.getReposUsingGET(org);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getReposUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **org** | **String**| org |

### Return type

[**List&lt;Repo&gt;**](Repo.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStageUsingGET"></a>
# **getStageUsingGET**
> Stage getStageUsingGET(app, branch, buildUniqueString, org, repo, stage)

getStage

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String buildUniqueString = "buildUniqueString_example"; // String | buildUniqueString
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
String stage = "stage_example"; // String | stage
try {
    Stage result = apiInstance.getStageUsingGET(app, branch, buildUniqueString, org, repo, stage);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getStageUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **buildUniqueString** | **String**| buildUniqueString |
 **org** | **String**| org |
 **repo** | **String**| repo |
 **stage** | **String**| stage |

### Return type

[**Stage**](Stage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStageUsingGET1"></a>
# **getStageUsingGET1**
> Stage getStageUsingGET1(app, branch, buildUniqueString, org, repo, stage)

getStage

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String buildUniqueString = "buildUniqueString_example"; // String | buildUniqueString
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
String stage = "stage_example"; // String | stage
try {
    Stage result = apiInstance.getStageUsingGET1(app, branch, buildUniqueString, org, repo, stage);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getStageUsingGET1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **buildUniqueString** | **String**| buildUniqueString |
 **org** | **String**| org |
 **repo** | **String**| repo |
 **stage** | **String**| stage |

### Return type

[**Stage**](Stage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStageUsingGET2"></a>
# **getStageUsingGET2**
> Stage getStageUsingGET2(app, branch, buildUniqueString, org, repo, stage)

getStage

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String buildUniqueString = "buildUniqueString_example"; // String | buildUniqueString
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
String stage = "stage_example"; // String | stage
try {
    Stage result = apiInstance.getStageUsingGET2(app, branch, buildUniqueString, org, repo, stage);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getStageUsingGET2");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **buildUniqueString** | **String**| buildUniqueString |
 **org** | **String**| org |
 **repo** | **String**| repo |
 **stage** | **String**| stage |

### Return type

[**Stage**](Stage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStageUsingGET3"></a>
# **getStageUsingGET3**
> Stage getStageUsingGET3(app, branch, buildUniqueString, org, repo, stage)

getStage

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String buildUniqueString = "buildUniqueString_example"; // String | buildUniqueString
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
String stage = "stage_example"; // String | stage
try {
    Stage result = apiInstance.getStageUsingGET3(app, branch, buildUniqueString, org, repo, stage);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getStageUsingGET3");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **buildUniqueString** | **String**| buildUniqueString |
 **org** | **String**| org |
 **repo** | **String**| repo |
 **stage** | **String**| stage |

### Return type

[**Stage**](Stage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStageUsingGET4"></a>
# **getStageUsingGET4**
> Stage getStageUsingGET4(app, branch, org, repo, stage)

getStage

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
String stage = "stage_example"; // String | stage
try {
    Stage result = apiInstance.getStageUsingGET4(app, branch, org, repo, stage);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getStageUsingGET4");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **org** | **String**| org |
 **repo** | **String**| repo |
 **stage** | **String**| stage |

### Return type

[**Stage**](Stage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStageUsingGET5"></a>
# **getStageUsingGET5**
> Stage getStageUsingGET5(app, branch, org, repo, stage)

getStage

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
String stage = "stage_example"; // String | stage
try {
    Stage result = apiInstance.getStageUsingGET5(app, branch, org, repo, stage);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getStageUsingGET5");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **org** | **String**| org |
 **repo** | **String**| repo |
 **stage** | **String**| stage |

### Return type

[**Stage**](Stage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStagesUsingGET"></a>
# **getStagesUsingGET**
> List&lt;Stage&gt; getStagesUsingGET(app, branch, org, repo)

getStages

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    List<Stage> result = apiInstance.getStagesUsingGET(app, branch, org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getStagesUsingGET");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**List&lt;Stage&gt;**](Stage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getStagesUsingGET1"></a>
# **getStagesUsingGET1**
> List&lt;Stage&gt; getStagesUsingGET1(app, branch, org, repo)

getStages

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.JsonControllerApi;


JsonControllerApi apiInstance = new JsonControllerApi();
String app = "app_example"; // String | app
String branch = "branch_example"; // String | branch
String org = "org_example"; // String | org
String repo = "repo_example"; // String | repo
try {
    List<Stage> result = apiInstance.getStagesUsingGET1(app, branch, org, repo);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling JsonControllerApi#getStagesUsingGET1");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **app** | **String**| app |
 **branch** | **String**| branch |
 **org** | **String**| org |
 **repo** | **String**| repo |

### Return type

[**List&lt;Stage&gt;**](Stage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

