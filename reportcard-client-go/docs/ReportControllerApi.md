# \ReportControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetTestResult**](ReportControllerApi.md#GetTestResult) | **Get** /api/v1/reports/{testResultId} | 
[**PostXml**](ReportControllerApi.md#PostXml) | **Post** /api/v1/reports | 
[**PostXmlJunit**](ReportControllerApi.md#PostXmlJunit) | **Post** /api/v1/reports/junit | 
[**PostXmlSurefire**](ReportControllerApi.md#PostXmlSurefire) | **Post** /api/v1/reports/surefire | 



## GetTestResult

> TestResult GetTestResult(ctx, testResultId).Execute()



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
    testResultId := "testResultId_example" // string | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.ReportControllerApi.GetTestResult(context.Background(), testResultId).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `ReportControllerApi.GetTestResult``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `GetTestResult`: TestResult
    fmt.Fprintf(os.Stdout, "Response from `ReportControllerApi.GetTestResult`: %v\n", resp)
}
```

### Path Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**ctx** | **context.Context** | context for authentication, logging, cancellation, deadlines, tracing, etc.
**testResultId** | **string** |  | 

### Other Parameters

Other parameters are passed through a pointer to a apiGetTestResultRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------


### Return type

[**TestResult**](TestResult.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## PostXml

> TestResult PostXml(ctx).Files(files).ReportMetaData(reportMetaData).Execute()



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
    files := []*os.File{"TODO"} // []*os.File | 
    reportMetaData := *openapiclient.NewReportMetaData() // ReportMetaData |  (optional)

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.ReportControllerApi.PostXml(context.Background()).Files(files).ReportMetaData(reportMetaData).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `ReportControllerApi.PostXml``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `PostXml`: TestResult
    fmt.Fprintf(os.Stdout, "Response from `ReportControllerApi.PostXml`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiPostXmlRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **files** | **[]*os.File** |  | 
 **reportMetaData** | [**ReportMetaData**](ReportMetaData.md) |  | 

### Return type

[**TestResult**](TestResult.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## PostXmlJunit

> TestResult PostXmlJunit(ctx).ReportMetaData(reportMetaData).InlineObject(inlineObject).Execute()



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
    reportMetaData := *openapiclient.NewReportMetaData() // ReportMetaData | 
    inlineObject := *openapiclient.NewInlineObject("TODO") // InlineObject |  (optional)

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.ReportControllerApi.PostXmlJunit(context.Background()).ReportMetaData(reportMetaData).InlineObject(inlineObject).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `ReportControllerApi.PostXmlJunit``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `PostXmlJunit`: TestResult
    fmt.Fprintf(os.Stdout, "Response from `ReportControllerApi.PostXmlJunit`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiPostXmlJunitRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **reportMetaData** | [**ReportMetaData**](ReportMetaData.md) |  | 
 **inlineObject** | [**InlineObject**](InlineObject.md) |  | 

### Return type

[**TestResult**](TestResult.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)


## PostXmlSurefire

> TestResult PostXmlSurefire(ctx).ReportMetaData(reportMetaData).Files(files).Execute()



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
    reportMetaData := *openapiclient.NewReportMetaData() // ReportMetaData | 
    files := []*os.File{"TODO"} // []*os.File | 

    configuration := openapiclient.NewConfiguration()
    api_client := openapiclient.NewAPIClient(configuration)
    resp, r, err := api_client.ReportControllerApi.PostXmlSurefire(context.Background()).ReportMetaData(reportMetaData).Files(files).Execute()
    if err != nil {
        fmt.Fprintf(os.Stderr, "Error when calling `ReportControllerApi.PostXmlSurefire``: %v\n", err)
        fmt.Fprintf(os.Stderr, "Full HTTP response: %v\n", r)
    }
    // response from `PostXmlSurefire`: TestResult
    fmt.Fprintf(os.Stdout, "Response from `ReportControllerApi.PostXmlSurefire`: %v\n", resp)
}
```

### Path Parameters



### Other Parameters

Other parameters are passed through a pointer to a apiPostXmlSurefireRequest struct via the builder pattern


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **reportMetaData** | [**ReportMetaData**](ReportMetaData.md) |  | 
 **files** | **[]*os.File** |  | 

### Return type

[**TestResult**](TestResult.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints)
[[Back to Model list]](../README.md#documentation-for-models)
[[Back to README]](../README.md)

