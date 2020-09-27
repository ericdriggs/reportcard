# ReportControllerApi

All URIs are relative to *https://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**postXmlJunitUsingPOST**](ReportControllerApi.md#postXmlJunitUsingPOST) | **POST** /v1/xml/junit | postXmlJunit
[**postXmlSurefireUsingPOST**](ReportControllerApi.md#postXmlSurefireUsingPOST) | **POST** /v1/xml/surefire | postXmlSurefire
[**postXmlUsingPOST**](ReportControllerApi.md#postXmlUsingPOST) | **POST** /v1/xml | postXml


<a name="postXmlJunitUsingPOST"></a>
# **postXmlJunitUsingPOST**
> TestResult postXmlJunitUsingPOST(file, reportMetatData)

postXmlJunit

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.ReportControllerApi;


ReportControllerApi apiInstance = new ReportControllerApi();
File file = new File("/path/to/file.txt"); // File | file
ReportMetaData reportMetatData = new ReportMetaData(); // ReportMetaData | reportMetatData
try {
    TestResult result = apiInstance.postXmlJunitUsingPOST(file, reportMetatData);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ReportControllerApi#postXmlJunitUsingPOST");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **File**| file |
 **reportMetatData** | [**ReportMetaData**](ReportMetaData.md)| reportMetatData |

### Return type

[**TestResult**](TestResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: */*

<a name="postXmlSurefireUsingPOST"></a>
# **postXmlSurefireUsingPOST**
> TestResult postXmlSurefireUsingPOST(files, reportMetatData)

postXmlSurefire

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.ReportControllerApi;


ReportControllerApi apiInstance = new ReportControllerApi();
List<File> files = Arrays.asList(new File("/path/to/file")); // List<File> | files
ReportMetaData reportMetatData = new ReportMetaData(); // ReportMetaData | reportMetatData
try {
    TestResult result = apiInstance.postXmlSurefireUsingPOST(files, reportMetatData);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ReportControllerApi#postXmlSurefireUsingPOST");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **files** | [**List&lt;File&gt;**](File.md)| files |
 **reportMetatData** | [**ReportMetaData**](ReportMetaData.md)| reportMetatData |

### Return type

[**TestResult**](TestResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

<a name="postXmlUsingPOST"></a>
# **postXmlUsingPOST**
> TestResult postXmlUsingPOST(files, reportMetatData)

postXml

### Example
```java
// Import classes:
//import com.ericdriggs.reportcard.client.invoker.ApiException;
//import com.ericdriggs.reportcard.client.api.ReportControllerApi;


ReportControllerApi apiInstance = new ReportControllerApi();
List<File> files = Arrays.asList(new File("/path/to/file")); // List<File> | files
ReportMetaData reportMetatData = new ReportMetaData(); // ReportMetaData | reportMetatData
try {
    TestResult result = apiInstance.postXmlUsingPOST(files, reportMetatData);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ReportControllerApi#postXmlUsingPOST");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **files** | [**List&lt;File&gt;**](File.md)| files |
 **reportMetatData** | [**ReportMetaData**](ReportMetaData.md)| reportMetatData |

### Return type

[**TestResult**](TestResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*

