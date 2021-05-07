# TestCase

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**TestCaseId** | Pointer to **int64** |  | [optional] 
**TestSuiteFk** | Pointer to **int64** |  | [optional] 
**Name** | Pointer to **string** |  | [optional] 
**ClassName** | Pointer to **string** |  | [optional] 
**Time** | Pointer to **float32** |  | [optional] 
**TestStatusFk** | Pointer to **string** |  | [optional] 
**TestStatus** | Pointer to **string** |  | [optional] 

## Methods

### NewTestCase

`func NewTestCase() *TestCase`

NewTestCase instantiates a new TestCase object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewTestCaseWithDefaults

`func NewTestCaseWithDefaults() *TestCase`

NewTestCaseWithDefaults instantiates a new TestCase object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTestCaseId

`func (o *TestCase) GetTestCaseId() int64`

GetTestCaseId returns the TestCaseId field if non-nil, zero value otherwise.

### GetTestCaseIdOk

`func (o *TestCase) GetTestCaseIdOk() (*int64, bool)`

GetTestCaseIdOk returns a tuple with the TestCaseId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestCaseId

`func (o *TestCase) SetTestCaseId(v int64)`

SetTestCaseId sets TestCaseId field to given value.

### HasTestCaseId

`func (o *TestCase) HasTestCaseId() bool`

HasTestCaseId returns a boolean if a field has been set.

### GetTestSuiteFk

`func (o *TestCase) GetTestSuiteFk() int64`

GetTestSuiteFk returns the TestSuiteFk field if non-nil, zero value otherwise.

### GetTestSuiteFkOk

`func (o *TestCase) GetTestSuiteFkOk() (*int64, bool)`

GetTestSuiteFkOk returns a tuple with the TestSuiteFk field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestSuiteFk

`func (o *TestCase) SetTestSuiteFk(v int64)`

SetTestSuiteFk sets TestSuiteFk field to given value.

### HasTestSuiteFk

`func (o *TestCase) HasTestSuiteFk() bool`

HasTestSuiteFk returns a boolean if a field has been set.

### GetName

`func (o *TestCase) GetName() string`

GetName returns the Name field if non-nil, zero value otherwise.

### GetNameOk

`func (o *TestCase) GetNameOk() (*string, bool)`

GetNameOk returns a tuple with the Name field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetName

`func (o *TestCase) SetName(v string)`

SetName sets Name field to given value.

### HasName

`func (o *TestCase) HasName() bool`

HasName returns a boolean if a field has been set.

### GetClassName

`func (o *TestCase) GetClassName() string`

GetClassName returns the ClassName field if non-nil, zero value otherwise.

### GetClassNameOk

`func (o *TestCase) GetClassNameOk() (*string, bool)`

GetClassNameOk returns a tuple with the ClassName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetClassName

`func (o *TestCase) SetClassName(v string)`

SetClassName sets ClassName field to given value.

### HasClassName

`func (o *TestCase) HasClassName() bool`

HasClassName returns a boolean if a field has been set.

### GetTime

`func (o *TestCase) GetTime() float32`

GetTime returns the Time field if non-nil, zero value otherwise.

### GetTimeOk

`func (o *TestCase) GetTimeOk() (*float32, bool)`

GetTimeOk returns a tuple with the Time field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTime

`func (o *TestCase) SetTime(v float32)`

SetTime sets Time field to given value.

### HasTime

`func (o *TestCase) HasTime() bool`

HasTime returns a boolean if a field has been set.

### GetTestStatusFk

`func (o *TestCase) GetTestStatusFk() string`

GetTestStatusFk returns the TestStatusFk field if non-nil, zero value otherwise.

### GetTestStatusFkOk

`func (o *TestCase) GetTestStatusFkOk() (*string, bool)`

GetTestStatusFkOk returns a tuple with the TestStatusFk field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestStatusFk

`func (o *TestCase) SetTestStatusFk(v string)`

SetTestStatusFk sets TestStatusFk field to given value.

### HasTestStatusFk

`func (o *TestCase) HasTestStatusFk() bool`

HasTestStatusFk returns a boolean if a field has been set.

### GetTestStatus

`func (o *TestCase) GetTestStatus() string`

GetTestStatus returns the TestStatus field if non-nil, zero value otherwise.

### GetTestStatusOk

`func (o *TestCase) GetTestStatusOk() (*string, bool)`

GetTestStatusOk returns a tuple with the TestStatus field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTestStatus

`func (o *TestCase) SetTestStatus(v string)`

SetTestStatus sets TestStatus field to given value.

### HasTestStatus

`func (o *TestCase) HasTestStatus() bool`

HasTestStatus returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


