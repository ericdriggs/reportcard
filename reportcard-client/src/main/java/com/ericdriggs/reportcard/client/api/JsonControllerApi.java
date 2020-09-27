package com.ericdriggs.reportcard.client.api;

import com.ericdriggs.reportcard.client.invoker.ApiClient;

import com.ericdriggs.reportcard.client.api.App;
import com.ericdriggs.reportcard.client.api.AppBranch;
import com.ericdriggs.reportcard.client.api.Branch;
import com.ericdriggs.reportcard.client.api.Build;
import com.ericdriggs.reportcard.client.api.Org;
import com.ericdriggs.reportcard.client.api.Repo;
import com.ericdriggs.reportcard.client.api.Stage;

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
@Component("com.ericdriggs.reportcard.client.api.JsonControllerApi")
public class JsonControllerApi {
    private ApiClient apiClient;

    public JsonControllerApi() {
        this(new ApiClient());
    }

    @Autowired
    public JsonControllerApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * getAppBranch
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param org org
     * @param repo repo
     * @return AppBranch
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public AppBranch getAppBranchUsingGET(String app, String branch, String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getAppBranchUsingGET");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getAppBranchUsingGET");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getAppBranchUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getAppBranchUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<AppBranch> returnType = new ParameterizedTypeReference<AppBranch>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getAppBranch
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param org org
     * @param repo repo
     * @return AppBranch
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public AppBranch getAppBranchUsingGET1(String app, String branch, String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getAppBranchUsingGET1");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getAppBranchUsingGET1");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getAppBranchUsingGET1");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getAppBranchUsingGET1");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<AppBranch> returnType = new ParameterizedTypeReference<AppBranch>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getApp
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param org org
     * @param repo repo
     * @return App
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public App getAppUsingGET(String app, String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getAppUsingGET");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getAppUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getAppUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/apps/{app}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<App> returnType = new ParameterizedTypeReference<App>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getApps
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param org org
     * @param repo repo
     * @return List&lt;App&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<App> getAppsUsingGET(String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getAppsUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getAppsUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/apps").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<List<App>> returnType = new ParameterizedTypeReference<List<App>>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getBranch
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param branch branch
     * @param org org
     * @param repo repo
     * @return Branch
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Branch getBranchUsingGET(String branch, String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getBranchUsingGET");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getBranchUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getBranchUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("branch", branch);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/branches/{branch}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Branch> returnType = new ParameterizedTypeReference<Branch>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getBranches
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param org org
     * @param repo repo
     * @return List&lt;Branch&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Branch> getBranchesUsingGET(String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getBranchesUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getBranchesUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/branches").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<List<Branch>> returnType = new ParameterizedTypeReference<List<Branch>>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getBuild
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param buildUniqueString buildUniqueString
     * @param org org
     * @param repo repo
     * @return Build
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Build getBuildUsingGET(String app, String branch, String buildUniqueString, String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getBuildUsingGET");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getBuildUsingGET");
        }
        
        // verify the required parameter 'buildUniqueString' is set
        if (buildUniqueString == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'buildUniqueString' when calling getBuildUsingGET");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getBuildUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getBuildUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("buildUniqueString", buildUniqueString);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds/{buildOrdinal}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Build> returnType = new ParameterizedTypeReference<Build>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getBuild
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param buildUniqueString buildUniqueString
     * @param org org
     * @param repo repo
     * @return Build
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Build getBuildUsingGET1(String app, String branch, String buildUniqueString, String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getBuildUsingGET1");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getBuildUsingGET1");
        }
        
        // verify the required parameter 'buildUniqueString' is set
        if (buildUniqueString == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'buildUniqueString' when calling getBuildUsingGET1");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getBuildUsingGET1");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getBuildUsingGET1");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("buildUniqueString", buildUniqueString);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/builds/{buildOrdinal}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Build> returnType = new ParameterizedTypeReference<Build>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getBuilds
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param org org
     * @param repo repo
     * @return List&lt;Build&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Build> getBuildsUsingGET(String app, String branch, String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getBuildsUsingGET");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getBuildsUsingGET");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getBuildsUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getBuildsUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<List<Build>> returnType = new ParameterizedTypeReference<List<Build>>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getOrg
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param org org
     * @return Org
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Org getOrgUsingGET(String org) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getOrgUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("org", org);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Org> returnType = new ParameterizedTypeReference<Org>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getOrgs
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param org org
     * @return List&lt;Org&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Org> getOrgsUsingGET(String org) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getOrgsUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("org", org);
        String path = UriComponentsBuilder.fromPath("/api/orgs").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<List<Org>> returnType = new ParameterizedTypeReference<List<Org>>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getRepo
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param org org
     * @param repo repo
     * @return Repo
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Repo getRepoUsingGET(String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getRepoUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getRepoUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Repo> returnType = new ParameterizedTypeReference<Repo>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getRepos
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param org org
     * @return List&lt;Repo&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Repo> getReposUsingGET(String org) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getReposUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("org", org);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<List<Repo>> returnType = new ParameterizedTypeReference<List<Repo>>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getStage
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param buildUniqueString buildUniqueString
     * @param org org
     * @param repo repo
     * @param stage stage
     * @return Stage
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Stage getStageUsingGET(String app, String branch, String buildUniqueString, String org, String repo, String stage) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getStageUsingGET");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getStageUsingGET");
        }
        
        // verify the required parameter 'buildUniqueString' is set
        if (buildUniqueString == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'buildUniqueString' when calling getStageUsingGET");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getStageUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getStageUsingGET");
        }
        
        // verify the required parameter 'stage' is set
        if (stage == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'stage' when calling getStageUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("buildUniqueString", buildUniqueString);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        uriVariables.put("stage", stage);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/builds/{buildOrdinal}/stages/{stage}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Stage> returnType = new ParameterizedTypeReference<Stage>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getStage
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param buildUniqueString buildUniqueString
     * @param org org
     * @param repo repo
     * @param stage stage
     * @return Stage
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Stage getStageUsingGET1(String app, String branch, String buildUniqueString, String org, String repo, String stage) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getStageUsingGET1");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getStageUsingGET1");
        }
        
        // verify the required parameter 'buildUniqueString' is set
        if (buildUniqueString == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'buildUniqueString' when calling getStageUsingGET1");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getStageUsingGET1");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getStageUsingGET1");
        }
        
        // verify the required parameter 'stage' is set
        if (stage == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'stage' when calling getStageUsingGET1");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("buildUniqueString", buildUniqueString);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        uriVariables.put("stage", stage);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages/{stage}/builds/{buildOrdinal}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Stage> returnType = new ParameterizedTypeReference<Stage>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getStage
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param buildUniqueString buildUniqueString
     * @param org org
     * @param repo repo
     * @param stage stage
     * @return Stage
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Stage getStageUsingGET2(String app, String branch, String buildUniqueString, String org, String repo, String stage) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getStageUsingGET2");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getStageUsingGET2");
        }
        
        // verify the required parameter 'buildUniqueString' is set
        if (buildUniqueString == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'buildUniqueString' when calling getStageUsingGET2");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getStageUsingGET2");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getStageUsingGET2");
        }
        
        // verify the required parameter 'stage' is set
        if (stage == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'stage' when calling getStageUsingGET2");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("buildUniqueString", buildUniqueString);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        uriVariables.put("stage", stage);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/builds/{buildOrdinal}/stages/{stage}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Stage> returnType = new ParameterizedTypeReference<Stage>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getStage
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param buildUniqueString buildUniqueString
     * @param org org
     * @param repo repo
     * @param stage stage
     * @return Stage
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Stage getStageUsingGET3(String app, String branch, String buildUniqueString, String org, String repo, String stage) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getStageUsingGET3");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getStageUsingGET3");
        }
        
        // verify the required parameter 'buildUniqueString' is set
        if (buildUniqueString == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'buildUniqueString' when calling getStageUsingGET3");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getStageUsingGET3");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getStageUsingGET3");
        }
        
        // verify the required parameter 'stage' is set
        if (stage == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'stage' when calling getStageUsingGET3");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("buildUniqueString", buildUniqueString);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        uriVariables.put("stage", stage);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages/{stage}/builds/{buildOrdinal}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Stage> returnType = new ParameterizedTypeReference<Stage>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getStage
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param org org
     * @param repo repo
     * @param stage stage
     * @return Stage
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Stage getStageUsingGET4(String app, String branch, String org, String repo, String stage) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getStageUsingGET4");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getStageUsingGET4");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getStageUsingGET4");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getStageUsingGET4");
        }
        
        // verify the required parameter 'stage' is set
        if (stage == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'stage' when calling getStageUsingGET4");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        uriVariables.put("stage", stage);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages/{stage}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Stage> returnType = new ParameterizedTypeReference<Stage>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getStage
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param org org
     * @param repo repo
     * @param stage stage
     * @return Stage
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Stage getStageUsingGET5(String app, String branch, String org, String repo, String stage) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getStageUsingGET5");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getStageUsingGET5");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getStageUsingGET5");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getStageUsingGET5");
        }
        
        // verify the required parameter 'stage' is set
        if (stage == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'stage' when calling getStageUsingGET5");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        uriVariables.put("stage", stage);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages/{stage}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Stage> returnType = new ParameterizedTypeReference<Stage>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getStages
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param org org
     * @param repo repo
     * @return List&lt;Stage&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Stage> getStagesUsingGET(String app, String branch, String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getStagesUsingGET");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getStagesUsingGET");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getStagesUsingGET");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getStagesUsingGET");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/apps/{app}/branches/{branch}/stages").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<List<Stage>> returnType = new ParameterizedTypeReference<List<Stage>>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
    /**
     * getStages
     * 
     * <p><b>200</b> - OK
     * <p><b>401</b> - Unauthorized
     * <p><b>403</b> - Forbidden
     * <p><b>404</b> - Not Found
     * @param app app
     * @param branch branch
     * @param org org
     * @param repo repo
     * @return List&lt;Stage&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public List<Stage> getStagesUsingGET1(String app, String branch, String org, String repo) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'app' is set
        if (app == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'app' when calling getStagesUsingGET1");
        }
        
        // verify the required parameter 'branch' is set
        if (branch == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'branch' when calling getStagesUsingGET1");
        }
        
        // verify the required parameter 'org' is set
        if (org == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'org' when calling getStagesUsingGET1");
        }
        
        // verify the required parameter 'repo' is set
        if (repo == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'repo' when calling getStagesUsingGET1");
        }
        
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("app", app);
        uriVariables.put("branch", branch);
        uriVariables.put("org", org);
        uriVariables.put("repo", repo);
        String path = UriComponentsBuilder.fromPath("/api/orgs/{org}/repos/{repo}/branches/{branch}/apps/{app}/stages").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = { 
            "application/json"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<List<Stage>> returnType = new ParameterizedTypeReference<List<Stage>>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
