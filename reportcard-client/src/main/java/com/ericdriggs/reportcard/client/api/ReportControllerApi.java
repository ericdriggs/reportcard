package com.ericdriggs.reportcard.client.api;

import com.ericdriggs.reportcard.client.invoker.ApiClient;

import java.io.File;
import com.ericdriggs.reportcard.client.api.ReportMetaData;
import com.ericdriggs.reportcard.client.api.TestResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-09-26T23:36:11.148-07:00")
@Component("com.ericdriggs.reportcard.client.api.ReportControllerApi")
public class ReportControllerApi {
    private ApiClient apiClient;

    public ReportControllerApi() {
        this(new ApiClient());
    }

    @Autowired
    public ReportControllerApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * postXmlJunit
     * 
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param file file
     * @param reportMetatData reportMetatData
     * @return TestResult
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public TestResult postXmlJunitUsingPOST(File file, ReportMetaData reportMetatData) throws RestClientException {
        Object postBody = reportMetatData;
        
        // verify the required parameter 'file' is set
        if (file == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'file' when calling postXmlJunitUsingPOST");
        }
        
        // verify the required parameter 'reportMetatData' is set
        if (reportMetatData == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'reportMetatData' when calling postXmlJunitUsingPOST");
        }
        
        String path = UriComponentsBuilder.fromPath("/v1/xml/junit").build().toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
        
        if (file != null)
            formParams.add("file", new FileSystemResource(file));

        final String[] accepts = { 
            "*/*"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { 
            "multipart/form-data"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<TestResult> returnType = new ParameterizedTypeReference<TestResult>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * postXmlSurefire
     * 
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param files files
     * @param reportMetatData reportMetatData
     * @return TestResult
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public TestResult postXmlSurefireUsingPOST(List<File> files, ReportMetaData reportMetatData) throws RestClientException {
        Object postBody = reportMetatData;
        
        // verify the required parameter 'files' is set
        if (files == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'files' when calling postXmlSurefireUsingPOST");
        }
        
        // verify the required parameter 'reportMetatData' is set
        if (reportMetatData == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'reportMetatData' when calling postXmlSurefireUsingPOST");
        }
        
        String path = UriComponentsBuilder.fromPath("/v1/xml/surefire").build().toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
        
        if (files != null)
            formParams.add("files", files);

        final String[] accepts = { 
            "*/*"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { 
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<TestResult> returnType = new ParameterizedTypeReference<TestResult>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * postXml
     * 
     * <p><b>200</b> - OK
     * <p><b>201</b> - Created
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param files files
     * @param reportMetatData reportMetatData
     * @return TestResult
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public TestResult postXmlUsingPOST(List<File> files, ReportMetaData reportMetatData) throws RestClientException {
        Object postBody = reportMetatData;
        
        // verify the required parameter 'files' is set
        if (files == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'files' when calling postXmlUsingPOST");
        }
        
        // verify the required parameter 'reportMetatData' is set
        if (reportMetatData == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'reportMetatData' when calling postXmlUsingPOST");
        }
        
        String path = UriComponentsBuilder.fromPath("/v1/xml").build().toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
        
        if (files != null)
            formParams.add("files", files);

        final String[] accepts = { 
            "*/*"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { 
            "application/json"
        };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<TestResult> returnType = new ParameterizedTypeReference<TestResult>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
