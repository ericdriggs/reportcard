# Branch

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**BranchId** | Pointer to **int32** |  | [optional] 
**BranchName** | Pointer to **string** |  | [optional] 
**RepoFk** | Pointer to **int32** |  | [optional] 

## Methods

### NewBranch

`func NewBranch() *Branch`

NewBranch instantiates a new Branch object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewBranchWithDefaults

`func NewBranchWithDefaults() *Branch`

NewBranchWithDefaults instantiates a new Branch object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetBranchId

`func (o *Branch) GetBranchId() int32`

GetBranchId returns the BranchId field if non-nil, zero value otherwise.

### GetBranchIdOk

`func (o *Branch) GetBranchIdOk() (*int32, bool)`

GetBranchIdOk returns a tuple with the BranchId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetBranchId

`func (o *Branch) SetBranchId(v int32)`

SetBranchId sets BranchId field to given value.

### HasBranchId

`func (o *Branch) HasBranchId() bool`

HasBranchId returns a boolean if a field has been set.

### GetBranchName

`func (o *Branch) GetBranchName() string`

GetBranchName returns the BranchName field if non-nil, zero value otherwise.

### GetBranchNameOk

`func (o *Branch) GetBranchNameOk() (*string, bool)`

GetBranchNameOk returns a tuple with the BranchName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetBranchName

`func (o *Branch) SetBranchName(v string)`

SetBranchName sets BranchName field to given value.

### HasBranchName

`func (o *Branch) HasBranchName() bool`

HasBranchName returns a boolean if a field has been set.

### GetRepoFk

`func (o *Branch) GetRepoFk() int32`

GetRepoFk returns the RepoFk field if non-nil, zero value otherwise.

### GetRepoFkOk

`func (o *Branch) GetRepoFkOk() (*int32, bool)`

GetRepoFkOk returns a tuple with the RepoFk field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRepoFk

`func (o *Branch) SetRepoFk(v int32)`

SetRepoFk sets RepoFk field to given value.

### HasRepoFk

`func (o *Branch) HasRepoFk() bool`

HasRepoFk returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


