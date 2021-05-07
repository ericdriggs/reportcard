# TestResult

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**TestResultId** | Pointer to **int64** |  | [optional] 
**StageFk** | Pointer to **int64** |  | [optional] 
**Tests** | Pointer to **int32** |  | [optional] 
**Skipped** | Pointer to **int32** |  | [optional] 
**Error** | Pointer to **int32** |  | [optional] 
**Failure** | Pointer to **int32** |  | [optional] 
**Time** | Pointer to **float32** |  | [optional] 
**TestResultCreated** | Pointer to **time.Time** |  | [optional] 
**ExternalLinks** | Pointer to **string** |  | [optional] 
**IsSuccess** | Pointer to **bool** |  | [optional] 
**HasSkip** | Pointer to **bool** |  | [optional] 
**TestSuites** | Pointer to [**[]TestSuite**](TestSuite.md) |  | [optional] 
**ExternalLinksMap** | Pointer to [**TestResult**](TestResult.md) |  | [optional] 

## Methods

### NewTestResult

`func NewTestResult() *TestResult`

NewTestResult instantiates a new TestResult object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewTestResultWithDefaults

`func NewTestResultWithDefaults() *TestResult`

NewTestResultWithDefaults instantiates a new TestResult object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTestResultId

`func (o *TestResult) GetTestResultId() int64`

GetTestResultId returns the TestResultId field if non-nil, zero value otherwise.

### GetTestResultIdOk

`func (o *TestResult) GetTestResultIdOk() (*int64, bool)`

GetTestResultIdOk returns a tuple with the TestResultId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestResultId

`func (o *TestResult) SetTestResultId(v int64)`

SetTestResultId sets TestResultId field to given value.

### HasTestResultId

`func (o *TestResult) HasTestResultId() bool`

HasTestResultId returns a boolean if a field has been set.

### GetStageFk

`func (o *TestResult) GetStageFk() int64`

GetStageFk returns the StageFk field if non-nil, zero value otherwise.

### GetStageFkOk

`func (o *TestResult) GetStageFkOk() (*int64, bool)`

GetStageFkOk returns a tuple with the StageFk field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetStageFk

`func (o *TestResult) SetStageFk(v int64)`

SetStageFk sets StageFk field to given value.

### HasStageFk

`func (o *TestResult) HasStageFk() bool`

HasStageFk returns a boolean if a field has been set.

### GetTests

`func (o *TestResult) GetTests() int32`

GetTests returns the Tests field if non-nil, zero value otherwise.

### GetTestsOk

`func (o *TestResult) GetTestsOk() (*int32, bool)`

GetTestsOk returns a tuple with the Tests field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTests

`func (o *TestResult) SetTests(v int32)`

SetTests sets Tests field to given value.

### HasTests

`func (o *TestResult) HasTests() bool`

HasTests returns a boolean if a field has been set.

### GetSkipped

`func (o *TestResult) GetSkipped() int32`

GetSkipped returns the Skipped field if non-nil, zero value otherwise.

### GetSkippedOk

`func (o *TestResult) GetSkippedOk() (*int32, bool)`

GetSkippedOk returns a tuple with the Skipped field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSkipped

`func (o *TestResult) SetSkipped(v int32)`

SetSkipped sets Skipped field to given value.

### HasSkipped

`func (o *TestResult) HasSkipped() bool`

HasSkipped returns a boolean if a field has been set.

### GetError

`func (o *TestResult) GetError() int32`

GetError returns the Error field if non-nil, zero value otherwise.

### GetErrorOk

`func (o *TestResult) GetErrorOk() (*int32, bool)`

GetErrorOk returns a tuple with the Error field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetError

`func (o *TestResult) SetError(v int32)`

SetError sets Error field to given value.

### HasError

`func (o *TestResult) HasError() bool`

HasError returns a boolean if a field has been set.

### GetFailure

`func (o *TestResult) GetFailure() int32`

GetFailure returns the Failure field if non-nil, zero value otherwise.

### GetFailureOk

`func (o *TestResult) GetFailureOk() (*int32, bool)`

GetFailureOk returns a tuple with the Failure field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFailure

`func (o *TestResult) SetFailure(v int32)`

SetFailure sets Failure field to given value.

### HasFailure

`func (o *TestResult) HasFailure() bool`

HasFailure returns a boolean if a field has been set.

### GetTime

`func (o *TestResult) GetTime() float32`

GetTime returns the Time field if non-nil, zero value otherwise.

### GetTimeOk

`func (o *TestResult) GetTimeOk() (*float32, bool)`

GetTimeOk returns a tuple with the Time field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTime

`func (o *TestResult) SetTime(v float32)`

SetTime sets Time field to given value.

### HasTime

`func (o *TestResult) HasTime() bool`

HasTime returns a boolean if a field has been set.

### GetTestResultCreated

`func (o *TestResult) GetTestResultCreated() time.Time`

GetTestResultCreated returns the TestResultCreated field if non-nil, zero value otherwise.

### GetTestResultCreatedOk

`func (o *TestResult) GetTestResultCreatedOk() (*time.Time, bool)`

GetTestResultCreatedOk returns a tuple with the TestResultCreated field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestResultCreated

`func (o *TestResult) SetTestResultCreated(v time.Time)`

SetTestResultCreated sets TestResultCreated field to given value.

### HasTestResultCreated

`func (o *TestResult) HasTestResultCreated() bool`

HasTestResultCreated returns a boolean if a field has been set.

### GetExternalLinks

`func (o *TestResult) GetExternalLinks() string`

GetExternalLinks returns the ExternalLinks field if non-nil, zero value otherwise.

### GetExternalLinksOk

`func (o *TestResult) GetExternalLinksOk() (*string, bool)`

GetExternalLinksOk returns a tuple with the ExternalLinks field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetExternalLinks

`func (o *TestResult) SetExternalLinks(v string)`

SetExternalLinks sets ExternalLinks field to given value.

### HasExternalLinks

`func (o *TestResult) HasExternalLinks() bool`

HasExternalLinks returns a boolean if a field has been set.

### GetIsSuccess

`func (o *TestResult) GetIsSuccess() bool`

GetIsSuccess returns the IsSuccess field if non-nil, zero value otherwise.

### GetIsSuccessOk

`func (o *TestResult) GetIsSuccessOk() (*bool, bool)`

GetIsSuccessOk returns a tuple with the IsSuccess field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetIsSuccess

`func (o *TestResult) SetIsSuccess(v bool)`

SetIsSuccess sets IsSuccess field to given value.

### HasIsSuccess

`func (o *TestResult) HasIsSuccess() bool`

HasIsSuccess returns a boolean if a field has been set.

### GetHasSkip

`func (o *TestResult) GetHasSkip() bool`

GetHasSkip returns the HasSkip field if non-nil, zero value otherwise.

### GetHasSkipOk

`func (o *TestResult) GetHasSkipOk() (*bool, bool)`

GetHasSkipOk returns a tuple with the HasSkip field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetHasSkip

`func (o *TestResult) SetHasSkip(v bool)`

SetHasSkip sets HasSkip field to given value.

### HasHasSkip

`func (o *TestResult) HasHasSkip() bool`

HasHasSkip returns a boolean if a field has been set.

### GetTestSuites

`func (o *TestResult) GetTestSuites() []TestSuite`

GetTestSuites returns the TestSuites field if non-nil, zero value otherwise.

### GetTestSuitesOk

`func (o *TestResult) GetTestSuitesOk() (*[]TestSuite, bool)`

GetTestSuitesOk returns a tuple with the TestSuites field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestSuites

`func (o *TestResult) SetTestSuites(v []TestSuite)`

SetTestSuites sets TestSuites field to given value.

### HasTestSuites

`func (o *TestResult) HasTestSuites() bool`

HasTestSuites returns a boolean if a field has been set.

### GetExternalLinksMap

`func (o *TestResult) GetExternalLinksMap() TestResult`

GetExternalLinksMap returns the ExternalLinksMap field if non-nil, zero value otherwise.

### GetExternalLinksMapOk

`func (o *TestResult) GetExternalLinksMapOk() (*TestResult, bool)`

GetExternalLinksMapOk returns a tuple with the ExternalLinksMap field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetExternalLinksMap

`func (o *TestResult) SetExternalLinksMap(v TestResult)`

SetExternalLinksMap sets ExternalLinksMap field to given value.

### HasExternalLinksMap

`func (o *TestResult) HasExternalLinksMap() bool`

HasExternalLinksMap returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


