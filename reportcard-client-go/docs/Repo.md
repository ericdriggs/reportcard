# Repo

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**RepoId** | Pointer to **int32** |  | [optional] 
**RepoName** | Pointer to **string** |  | [optional] 
**OrgFk** | Pointer to **int32** |  | [optional] 

## Methods

### NewRepo

`func NewRepo() *Repo`

NewRepo instantiates a new Repo object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewRepoWithDefaults

`func NewRepoWithDefaults() *Repo`

NewRepoWithDefaults instantiates a new Repo object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetRepoId

`func (o *Repo) GetRepoId() int32`

GetRepoId returns the RepoId field if non-nil, zero value otherwise.

### GetRepoIdOk

`func (o *Repo) GetRepoIdOk() (*int32, bool)`

GetRepoIdOk returns a tuple with the RepoId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRepoId

`func (o *Repo) SetRepoId(v int32)`

SetRepoId sets RepoId field to given value.

### HasRepoId

`func (o *Repo) HasRepoId() bool`

HasRepoId returns a boolean if a field has been set.

### GetRepoName

`func (o *Repo) GetRepoName() string`

GetRepoName returns the RepoName field if non-nil, zero value otherwise.

### GetRepoNameOk

`func (o *Repo) GetRepoNameOk() (*string, bool)`

GetRepoNameOk returns a tuple with the RepoName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRepoName

`func (o *Repo) SetRepoName(v string)`

SetRepoName sets RepoName field to given value.

### HasRepoName

`func (o *Repo) HasRepoName() bool`

HasRepoName returns a boolean if a field has been set.

### GetOrgFk

`func (o *Repo) GetOrgFk() int32`

GetOrgFk returns the OrgFk field if non-nil, zero value otherwise.

### GetOrgFkOk

`func (o *Repo) GetOrgFkOk() (*int32, bool)`

GetOrgFkOk returns a tuple with the OrgFk field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOrgFk

`func (o *Repo) SetOrgFk(v int32)`

SetOrgFk sets OrgFk field to given value.

### HasOrgFk

`func (o *Repo) HasOrgFk() bool`

HasOrgFk returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


