# \MetadataJsonControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetBranch**](MetadataJsonControllerApi.md#GetBranch) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches/{branch} | 
[**GetBranches**](MetadataJsonControllerApi.md#GetBranches) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches | 
[**GetContext**](MetadataJsonControllerApi.md#GetContext) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host} | 
[**GetContexts**](MetadataJsonControllerApi.md#GetContexts) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts | 
[**GetExecution**](MetadataJsonControllerApi.md#GetExecution) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/executions/{externalExecutionId} | 
[**GetExecutions**](MetadataJsonControllerApi.md#GetExecutions) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/executions | 
[**GetOrg**](MetadataJsonControllerApi.md#GetOrg) | **Get** /api/v1/orgs/{org} | 
[**GetOrgs**](MetadataJsonControllerApi.md#GetOrgs) | **Get** /api/v1/orgs | 
[**GetRepo**](MetadataJsonControllerApi.md#GetRepo) | **Get** /api/v1/orgs/{org}/repos/{repo} | 
[**GetRepos**](MetadataJsonControllerApi.md#GetRepos) | **Get** /api/v1/orgs/{org}/repos | 
[**GetSha**](MetadataJsonControllerApi.md#GetSha) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches/{branch}/shas/{sha} | 
[**GetShas**](MetadataJsonControllerApi.md#GetShas) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches/{branch}/shas | 
[**GetStage**](MetadataJsonControllerApi.md#GetStage) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/executions/{executionName}/stages/{stage} | 
[**GetStages**](MetadataJsonControllerApi.md#GetStages) | **Get** /api/v1/orgs/{org}/repos/{repo}/branches/{branch}/shas/{sha}/contexts/{host}/executions/{executionName}/stages | 



## GetBranch

> map[string][]Sha GetBranch(ctx, org, repo, branch).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 
    branch := "branch_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetBranch(context.Background(), org, repo, branch).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetBranch``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetBranch`: map[string][]Sha
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetBranch`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 
**branch** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetBranchRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------




### Return type

[**map[string][]Sha**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetBranches

> map[string][]Sha GetBranches(ctx, org, repo).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetBranches(context.Background(), org, repo).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetBranches``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetBranches`: map[string][]Sha
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetBranches`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetBranchesRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------



### Return type

[**map[string][]Sha**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetContext

> map[string][]Execution GetContext(ctx, org, repo, branch, sha, host).Application(application).Pipeline(pipeline).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 
    branch := "branch_example" // string | 
    sha := "sha_example" // string | 
    host := "host_example" // string | 
    application := "application_example" // string | 
    pipeline := "pipeline_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetContext(context.Background(), org, repo, branch, sha, host).Application(application).Pipeline(pipeline).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetContext``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetContext`: map[string][]Execution
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetContext`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 
**branch** | **string** |  | 
**sha** | **string** |  | 
**host** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetContextRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------





 **application** | **string** |  | 
 **pipeline** | **string** |  | 

### Return type

[**map[string][]Execution**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetContexts

> map[string][]Execution GetContexts(ctx, org, repo, branch, sha).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 
    branch := "branch_example" // string | 
    sha := "sha_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetContexts(context.Background(), org, repo, branch, sha).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetContexts``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetContexts`: map[string][]Execution
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetContexts`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 
**branch** | **string** |  | 
**sha** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetContextsRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------





### Return type

[**map[string][]Execution**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetExecution

> map[string][]Stage GetExecution(ctx, org, repo, branch, sha, host, externalExecutionId).Application(application).Pipeline(pipeline).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 
    branch := "branch_example" // string | 
    sha := "sha_example" // string | 
    host := "host_example" // string | 
    externalExecutionId := "externalExecutionId_example" // string | 
    application := "application_example" // string | 
    pipeline := "pipeline_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetExecution(context.Background(), org, repo, branch, sha, host, externalExecutionId).Application(application).Pipeline(pipeline).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetExecution``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetExecution`: map[string][]Stage
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetExecution`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 
**branch** | **string** |  | 
**sha** | **string** |  | 
**host** | **string** |  | 
**externalExecutionId** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetExecutionRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------






 **application** | **string** |  | 
 **pipeline** | **string** |  | 

### Return type

[**map[string][]Stage**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetExecutions

> map[string][]Stage GetExecutions(ctx, org, repo, branch, sha, host).Application(application).Pipeline(pipeline).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 
    branch := "branch_example" // string | 
    sha := "sha_example" // string | 
    host := "host_example" // string | 
    application := "application_example" // string | 
    pipeline := "pipeline_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetExecutions(context.Background(), org, repo, branch, sha, host).Application(application).Pipeline(pipeline).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetExecutions``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetExecutions`: map[string][]Stage
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetExecutions`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 
**branch** | **string** |  | 
**sha** | **string** |  | 
**host** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetExecutionsRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------





 **application** | **string** |  | 
 **pipeline** | **string** |  | 

### Return type

[**map[string][]Stage**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetOrg

> map[string][]Repo GetOrg(ctx, org).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetOrg(context.Background(), org).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetOrg``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetOrg`: map[string][]Repo
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetOrg`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetOrgRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**map[string][]Repo**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetOrgs

> map[string][]Repo GetOrgs(ctx).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetOrgs(context.Background()).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetOrgs``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetOrgs`: map[string][]Repo
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetOrgs`: %v\n", resp)
}
```

### Path Parameters

This endpoint does not need any parameter.

### Other Parameters

Other parameters are passed through a pointer to a apiGetOrgsRequest struct via the builder pattern


### Return type

[**map[string][]Repo**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetRepo

> map[string][]Branch GetRepo(ctx, org, repo).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetRepo(context.Background(), org, repo).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetRepo``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetRepo`: map[string][]Branch
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetRepo`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetRepoRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------



### Return type

[**map[string][]Branch**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetRepos

> map[string][]Branch GetRepos(ctx, org).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetRepos(context.Background(), org).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetRepos``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetRepos`: map[string][]Branch
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetRepos`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetReposRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**map[string][]Branch**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetSha

> map[string][]Context GetSha(ctx, org, repo, branch, sha).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 
    branch := "branch_example" // string | 
    sha := "sha_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetSha(context.Background(), org, repo, branch, sha).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetSha``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetSha`: map[string][]Context
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetSha`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 
**branch** | **string** |  | 
**sha** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetShaRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------





### Return type

[**map[string][]Context**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetShas

> map[string][]Context GetShas(ctx, org, repo, branch).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 
    branch := "branch_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetShas(context.Background(), org, repo, branch).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetShas``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetShas`: map[string][]Context
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetShas`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 
**branch** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetShasRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------




### Return type

[**map[string][]Context**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetStage

> map[string][]TestResult GetStage(ctx, org, repo, branch, sha, host, executionName, stage).Application(application).Pipeline(pipeline).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 
    branch := "branch_example" // string | 
    sha := "sha_example" // string | 
    host := "host_example" // string | 
    executionName := "executionName_example" // string | 
    stage := "stage_example" // string | 
    application := "application_example" // string | 
    pipeline := "pipeline_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetStage(context.Background(), org, repo, branch, sha, host, executionName, stage).Application(application).Pipeline(pipeline).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetStage``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetStage`: map[string][]TestResult
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetStage`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 
**branch** | **string** |  | 
**sha** | **string** |  | 
**host** | **string** |  | 
**executionName** | **string** |  | 
**stage** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetStageRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------







 **application** | **string** |  | 
 **pipeline** | **string** |  | 

### Return type

[**map[string][]TestResult**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## GetStages

> map[string][]TestResult GetStages(ctx, org, repo, branch, sha, host, executionName).Application(application).Pipeline(pipeline).Execute()



### Example

```go
package main

import (
    "context"
    "fmt"
    "os"
    openapiclient "./openapi"
)

func main() {
    org := "org_example" // string | 
    repo := "repo_example" // string | 
    branch := "branch_example" // string | 
    sha := "sha_example" // string | 
    host := "host_example" // string | 
    executionName := "executionName_example" // string | 
    application := "application_example" // string | 
    pipeline := "pipeline_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.MetadataJsonControllerApi.GetStages(context.Background(), org, repo, branch, sha, host, executionName).Application(application).Pipeline(pipeline).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `MetadataJsonControllerApi.GetStages``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetStages`: map[string][]TestResult
    fmt.Fprintf(os.Stdout, "Response from `MetadataJsonControllerApi.GetStages`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**org** | **string** |  | 
**repo** | **string** |  | 
**branch** | **string** |  | 
**sha** | **string** |  | 
**host** | **string** |  | 
**executionName** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetStagesRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------






 **application** | **string** |  | 
 **pipeline** | **string** |  | 

### Return type

[**map[string][]TestResult**](set.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

