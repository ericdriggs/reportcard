# Context

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ContextId** | Pointer to **int64** |  | [optional] 
**ShaFk** | Pointer to **int64** |  | [optional] 
**Host** | Pointer to **string** |  | [optional] 
**Application** | Pointer to **string** |  | [optional] 
**Pipeline** | Pointer to **string** |  | [optional] 

## Methods

### NewContext

`func NewContext() *Context`

NewContext instantiates a new Context object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewContextWithDefaults

`func NewContextWithDefaults() *Context`

NewContextWithDefaults instantiates a new Context object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetContextId

`func (o *Context) GetContextId() int64`

GetContextId returns the ContextId field if non-nil, zero value otherwise.

### GetContextIdOk

`func (o *Context) GetContextIdOk() (*int64, bool)`

GetContextIdOk returns a tuple with the ContextId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetContextId

`func (o *Context) SetContextId(v int64)`

SetContextId sets ContextId field to given value.

### HasContextId

`func (o *Context) HasContextId() bool`

HasContextId returns a boolean if a field has been set.

### GetShaFk

`func (o *Context) GetShaFk() int64`

GetShaFk returns the ShaFk field if non-nil, zero value otherwise.

### GetShaFkOk

`func (o *Context) GetShaFkOk() (*int64, bool)`

GetShaFkOk returns a tuple with the ShaFk field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetShaFk

`func (o *Context) SetShaFk(v int64)`

SetShaFk sets ShaFk field to given value.

### HasShaFk

`func (o *Context) HasShaFk() bool`

HasShaFk returns a boolean if a field has been set.

### GetHost

`func (o *Context) GetHost() string`

GetHost returns the Host field if non-nil, zero value otherwise.

### GetHostOk

`func (o *Context) GetHostOk() (*string, bool)`

GetHostOk returns a tuple with the Host field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetHost

`func (o *Context) SetHost(v string)`

SetHost sets Host field to given value.

### HasHost

`func (o *Context) HasHost() bool`

HasHost returns a boolean if a field has been set.

### GetApplication

`func (o *Context) GetApplication() string`

GetApplication returns the Application field if non-nil, zero value otherwise.

### GetApplicationOk

`func (o *Context) GetApplicationOk() (*string, bool)`

GetApplicationOk returns a tuple with the Application field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetApplication

`func (o *Context) SetApplication(v string)`

SetApplication sets Application field to given value.

### HasApplication

`func (o *Context) HasApplication() bool`

HasApplication returns a boolean if a field has been set.

### GetPipeline

`func (o *Context) GetPipeline() string`

GetPipeline returns the Pipeline field if non-nil, zero value otherwise.

### GetPipelineOk

`func (o *Context) GetPipelineOk() (*string, bool)`

GetPipelineOk returns a tuple with the Pipeline field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPipeline

`func (o *Context) SetPipeline(v string)`

SetPipeline sets Pipeline field to given value.

### HasPipeline

`func (o *Context) HasPipeline() bool`

HasPipeline returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


