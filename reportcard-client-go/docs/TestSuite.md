# TestSuite

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**TestSuiteId** | Pointer to **int64** |  | [optional] 
**TestResultFk** | Pointer to **int64** |  | [optional] 
**Tests** | Pointer to **int32** |  | [optional] 
**Skipped** | Pointer to **int32** |  | [optional] 
**Error** | Pointer to **int32** |  | [optional] 
**Failure** | Pointer to **int32** |  | [optional] 
**Time** | Pointer to **float32** |  | [optional] 
**Group** | Pointer to **string** |  | [optional] 
**Properties** | Pointer to **string** |  | [optional] 
**IsSuccess** | Pointer to **bool** |  | [optional] 
**HasSkip** | Pointer to **bool** |  | [optional] 
**TestCases** | Pointer to [**[]TestCase**](TestCase.md) |  | [optional] 
**Package** | Pointer to **string** |  | [optional] 

## Methods

### NewTestSuite

`func NewTestSuite() *TestSuite`

NewTestSuite instantiates a new TestSuite object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewTestSuiteWithDefaults

`func NewTestSuiteWithDefaults() *TestSuite`

NewTestSuiteWithDefaults instantiates a new TestSuite object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTestSuiteId

`func (o *TestSuite) GetTestSuiteId() int64`

GetTestSuiteId returns the TestSuiteId field if non-nil, zero value otherwise.

### GetTestSuiteIdOk

`func (o *TestSuite) GetTestSuiteIdOk() (*int64, bool)`

GetTestSuiteIdOk returns a tuple with the TestSuiteId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestSuiteId

`func (o *TestSuite) SetTestSuiteId(v int64)`

SetTestSuiteId sets TestSuiteId field to given value.

### HasTestSuiteId

`func (o *TestSuite) HasTestSuiteId() bool`

HasTestSuiteId returns a boolean if a field has been set.

### GetTestResultFk

`func (o *TestSuite) GetTestResultFk() int64`

GetTestResultFk returns the TestResultFk field if non-nil, zero value otherwise.

### GetTestResultFkOk

`func (o *TestSuite) GetTestResultFkOk() (*int64, bool)`

GetTestResultFkOk returns a tuple with the TestResultFk field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestResultFk

`func (o *TestSuite) SetTestResultFk(v int64)`

SetTestResultFk sets TestResultFk field to given value.

### HasTestResultFk

`func (o *TestSuite) HasTestResultFk() bool`

HasTestResultFk returns a boolean if a field has been set.

### GetTests

`func (o *TestSuite) GetTests() int32`

GetTests returns the Tests field if non-nil, zero value otherwise.

### GetTestsOk

`func (o *TestSuite) GetTestsOk() (*int32, bool)`

GetTestsOk returns a tuple with the Tests field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTests

`func (o *TestSuite) SetTests(v int32)`

SetTests sets Tests field to given value.

### HasTests

`func (o *TestSuite) HasTests() bool`

HasTests returns a boolean if a field has been set.

### GetSkipped

`func (o *TestSuite) GetSkipped() int32`

GetSkipped returns the Skipped field if non-nil, zero value otherwise.

### GetSkippedOk

`func (o *TestSuite) GetSkippedOk() (*int32, bool)`

GetSkippedOk returns a tuple with the Skipped field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSkipped

`func (o *TestSuite) SetSkipped(v int32)`

SetSkipped sets Skipped field to given value.

### HasSkipped

`func (o *TestSuite) HasSkipped() bool`

HasSkipped returns a boolean if a field has been set.

### GetError

`func (o *TestSuite) GetError() int32`

GetError returns the Error field if non-nil, zero value otherwise.

### GetErrorOk

`func (o *TestSuite) GetErrorOk() (*int32, bool)`

GetErrorOk returns a tuple with the Error field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetError

`func (o *TestSuite) SetError(v int32)`

SetError sets Error field to given value.

### HasError

`func (o *TestSuite) HasError() bool`

HasError returns a boolean if a field has been set.

### GetFailure

`func (o *TestSuite) GetFailure() int32`

GetFailure returns the Failure field if non-nil, zero value otherwise.

### GetFailureOk

`func (o *TestSuite) GetFailureOk() (*int32, bool)`

GetFailureOk returns a tuple with the Failure field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFailure

`func (o *TestSuite) SetFailure(v int32)`

SetFailure sets Failure field to given value.

### HasFailure

`func (o *TestSuite) HasFailure() bool`

HasFailure returns a boolean if a field has been set.

### GetTime

`func (o *TestSuite) GetTime() float32`

GetTime returns the Time field if non-nil, zero value otherwise.

### GetTimeOk

`func (o *TestSuite) GetTimeOk() (*float32, bool)`

GetTimeOk returns a tuple with the Time field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTime

`func (o *TestSuite) SetTime(v float32)`

SetTime sets Time field to given value.

### HasTime

`func (o *TestSuite) HasTime() bool`

HasTime returns a boolean if a field has been set.

### GetGroup

`func (o *TestSuite) GetGroup() string`

GetGroup returns the Group field if non-nil, zero value otherwise.

### GetGroupOk

`func (o *TestSuite) GetGroupOk() (*string, bool)`

GetGroupOk returns a tuple with the Group field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetGroup

`func (o *TestSuite) SetGroup(v string)`

SetGroup sets Group field to given value.

### HasGroup

`func (o *TestSuite) HasGroup() bool`

HasGroup returns a boolean if a field has been set.

### GetProperties

`func (o *TestSuite) GetProperties() string`

GetProperties returns the Properties field if non-nil, zero value otherwise.

### GetPropertiesOk

`func (o *TestSuite) GetPropertiesOk() (*string, bool)`

GetPropertiesOk returns a tuple with the Properties field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetProperties

`func (o *TestSuite) SetProperties(v string)`

SetProperties sets Properties field to given value.

### HasProperties

`func (o *TestSuite) HasProperties() bool`

HasProperties returns a boolean if a field has been set.

### GetIsSuccess

`func (o *TestSuite) GetIsSuccess() bool`

GetIsSuccess returns the IsSuccess field if non-nil, zero value otherwise.

### GetIsSuccessOk

`func (o *TestSuite) GetIsSuccessOk() (*bool, bool)`

GetIsSuccessOk returns a tuple with the IsSuccess field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetIsSuccess

`func (o *TestSuite) SetIsSuccess(v bool)`

SetIsSuccess sets IsSuccess field to given value.

### HasIsSuccess

`func (o *TestSuite) HasIsSuccess() bool`

HasIsSuccess returns a boolean if a field has been set.

### GetHasSkip

`func (o *TestSuite) GetHasSkip() bool`

GetHasSkip returns the HasSkip field if non-nil, zero value otherwise.

### GetHasSkipOk

`func (o *TestSuite) GetHasSkipOk() (*bool, bool)`

GetHasSkipOk returns a tuple with the HasSkip field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetHasSkip

`func (o *TestSuite) SetHasSkip(v bool)`

SetHasSkip sets HasSkip field to given value.

### HasHasSkip

`func (o *TestSuite) HasHasSkip() bool`

HasHasSkip returns a boolean if a field has been set.

### GetTestCases

`func (o *TestSuite) GetTestCases() []TestCase`

GetTestCases returns the TestCases field if non-nil, zero value otherwise.

### GetTestCasesOk

`func (o *TestSuite) GetTestCasesOk() (*[]TestCase, bool)`

GetTestCasesOk returns a tuple with the TestCases field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestCases

`func (o *TestSuite) SetTestCases(v []TestCase)`

SetTestCases sets TestCases field to given value.

### HasTestCases

`func (o *TestSuite) HasTestCases() bool`

HasTestCases returns a boolean if a field has been set.

### GetPackage

`func (o *TestSuite) GetPackage() string`

GetPackage returns the Package field if non-nil, zero value otherwise.

### GetPackageOk

`func (o *TestSuite) GetPackageOk() (*string, bool)`

GetPackageOk returns a tuple with the Package field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPackage

`func (o *TestSuite) SetPackage(v string)`

SetPackage sets Package field to given value.

### HasPackage

`func (o *TestSuite) HasPackage() bool`

HasPackage returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


