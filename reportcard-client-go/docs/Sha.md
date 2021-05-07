# Sha

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ShaId** | Pointer to **int64** |  | [optional] 
**Sha** | Pointer to **string** |  | [optional] 
**ShaCreated** | Pointer to **time.Time** |  | [optional] 
**BranchFk** | Pointer to **int32** |  | [optional] 

## Methods

### NewSha

`func NewSha() *Sha`

NewSha instantiates a new Sha object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewShaWithDefaults

`func NewShaWithDefaults() *Sha`

NewShaWithDefaults instantiates a new Sha object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetShaId

`func (o *Sha) GetShaId() int64`

GetShaId returns the ShaId field if non-nil, zero value otherwise.

### GetShaIdOk

`func (o *Sha) GetShaIdOk() (*int64, bool)`

GetShaIdOk returns a tuple with the ShaId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetShaId

`func (o *Sha) SetShaId(v int64)`

SetShaId sets ShaId field to given value.

### HasShaId

`func (o *Sha) HasShaId() bool`

HasShaId returns a boolean if a field has been set.

### GetSha

`func (o *Sha) GetSha() string`

GetSha returns the Sha field if non-nil, zero value otherwise.

### GetShaOk

`func (o *Sha) GetShaOk() (*string, bool)`

GetShaOk returns a tuple with the Sha field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSha

`func (o *Sha) SetSha(v string)`

SetSha sets Sha field to given value.

### HasSha

`func (o *Sha) HasSha() bool`

HasSha returns a boolean if a field has been set.

### GetShaCreated

`func (o *Sha) GetShaCreated() time.Time`

GetShaCreated returns the ShaCreated field if non-nil, zero value otherwise.

### GetShaCreatedOk

`func (o *Sha) GetShaCreatedOk() (*time.Time, bool)`

GetShaCreatedOk returns a tuple with the ShaCreated field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetShaCreated

`func (o *Sha) SetShaCreated(v time.Time)`

SetShaCreated sets ShaCreated field to given value.

### HasShaCreated

`func (o *Sha) HasShaCreated() bool`

HasShaCreated returns a boolean if a field has been set.

### GetBranchFk

`func (o *Sha) GetBranchFk() int32`

GetBranchFk returns the BranchFk field if non-nil, zero value otherwise.

### GetBranchFkOk

`func (o *Sha) GetBranchFkOk() (*int32, bool)`

GetBranchFkOk returns a tuple with the BranchFk field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetBranchFk

`func (o *Sha) SetBranchFk(v int32)`

SetBranchFk sets BranchFk field to given value.

### HasBranchFk

`func (o *Sha) HasBranchFk() bool`

HasBranchFk returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


