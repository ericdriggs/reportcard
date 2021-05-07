# Execution

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ExecutionId** | Pointer to **int64** |  | [optional] 
**ExecutionExternalId** | Pointer to **string** |  | [optional] 
**ContextFk** | Pointer to **int64** |  | [optional] 

## Methods

### NewExecution

`func NewExecution() *Execution`

NewExecution instantiates a new Execution object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewExecutionWithDefaults

`func NewExecutionWithDefaults() *Execution`

NewExecutionWithDefaults instantiates a new Execution object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetExecutionId

`func (o *Execution) GetExecutionId() int64`

GetExecutionId returns the ExecutionId field if non-nil, zero value otherwise.

### GetExecutionIdOk

`func (o *Execution) GetExecutionIdOk() (*int64, bool)`

GetExecutionIdOk returns a tuple with the ExecutionId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetExecutionId

`func (o *Execution) SetExecutionId(v int64)`

SetExecutionId sets ExecutionId field to given value.

### HasExecutionId

`func (o *Execution) HasExecutionId() bool`

HasExecutionId returns a boolean if a field has been set.

### GetExecutionExternalId

`func (o *Execution) GetExecutionExternalId() string`

GetExecutionExternalId returns the ExecutionExternalId field if non-nil, zero value otherwise.

### GetExecutionExternalIdOk

`func (o *Execution) GetExecutionExternalIdOk() (*string, bool)`

GetExecutionExternalIdOk returns a tuple with the ExecutionExternalId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetExecutionExternalId

`func (o *Execution) SetExecutionExternalId(v string)`

SetExecutionExternalId sets ExecutionExternalId field to given value.

### HasExecutionExternalId

`func (o *Execution) HasExecutionExternalId() bool`

HasExecutionExternalId returns a boolean if a field has been set.

### GetContextFk

`func (o *Execution) GetContextFk() int64`

GetContextFk returns the ContextFk field if non-nil, zero value otherwise.

### GetContextFkOk

`func (o *Execution) GetContextFkOk() (*int64, bool)`

GetContextFkOk returns a tuple with the ContextFk field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetContextFk

`func (o *Execution) SetContextFk(v int64)`

SetContextFk sets ContextFk field to given value.

### HasContextFk

`func (o *Execution) HasContextFk() bool`

HasContextFk returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


