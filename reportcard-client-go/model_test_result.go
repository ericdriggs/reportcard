/*
 * OpenAPI definition
 *
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * API version: v0
 */

// Code generated by OpenAPI Generator (https://openapi-generator.tech); DO NOT EDIT.

package openapi

import (
	"encoding/json"
	"time"
)

// TestResult struct for TestResult
type TestResult struct {
	TestResultId *int64 `json:"testResultId,omitempty"`
	StageFk *int64 `json:"stageFk,omitempty"`
	Tests *int32 `json:"tests,omitempty"`
	Skipped *int32 `json:"skipped,omitempty"`
	Error *int32 `json:"error,omitempty"`
	Failure *int32 `json:"failure,omitempty"`
	Time *float32 `json:"time,omitempty"`
	TestResultCreated *time.Time `json:"testResultCreated,omitempty"`
	ExternalLinks *string `json:"externalLinks,omitempty"`
	IsSuccess *bool `json:"isSuccess,omitempty"`
	HasSkip *bool `json:"hasSkip,omitempty"`
	TestSuites *[]TestSuite `json:"testSuites,omitempty"`
	ExternalLinksMap *TestResult `json:"externalLinksMap,omitempty"`
}

// NewTestResult instantiates a new TestResult object
// This constructor will assign default values to properties that have it defined,
// and makes sure properties required by API are set, but the set of arguments
// will change when the set of required properties is changed
func NewTestResult() *TestResult {
	this := TestResult{}
	return &this
}

// NewTestResultWithDefaults instantiates a new TestResult object
// This constructor will only assign default values to properties that have it defined,
// but it doesn't guarantee that properties required by API are set
func NewTestResultWithDefaults() *TestResult {
	this := TestResult{}
	return &this
}

// GetTestResultId returns the TestResultId field value if set, zero value otherwise.
func (o *TestResult) GetTestResultId() int64 {
	if o == nil || o.TestResultId == nil {
		var ret int64
		return ret
	}
	return *o.TestResultId
}

// GetTestResultIdOk returns a tuple with the TestResultId field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetTestResultIdOk() (*int64, bool) {
	if o == nil || o.TestResultId == nil {
		return nil, false
	}
	return o.TestResultId, true
}

// HasTestResultId returns a boolean if a field has been set.
func (o *TestResult) HasTestResultId() bool {
	if o != nil && o.TestResultId != nil {
		return true
	}

	return false
}

// SetTestResultId gets a reference to the given int64 and assigns it to the TestResultId field.
func (o *TestResult) SetTestResultId(v int64) {
	o.TestResultId = &v
}

// GetStageFk returns the StageFk field value if set, zero value otherwise.
func (o *TestResult) GetStageFk() int64 {
	if o == nil || o.StageFk == nil {
		var ret int64
		return ret
	}
	return *o.StageFk
}

// GetStageFkOk returns a tuple with the StageFk field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetStageFkOk() (*int64, bool) {
	if o == nil || o.StageFk == nil {
		return nil, false
	}
	return o.StageFk, true
}

// HasStageFk returns a boolean if a field has been set.
func (o *TestResult) HasStageFk() bool {
	if o != nil && o.StageFk != nil {
		return true
	}

	return false
}

// SetStageFk gets a reference to the given int64 and assigns it to the StageFk field.
func (o *TestResult) SetStageFk(v int64) {
	o.StageFk = &v
}

// GetTests returns the Tests field value if set, zero value otherwise.
func (o *TestResult) GetTests() int32 {
	if o == nil || o.Tests == nil {
		var ret int32
		return ret
	}
	return *o.Tests
}

// GetTestsOk returns a tuple with the Tests field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetTestsOk() (*int32, bool) {
	if o == nil || o.Tests == nil {
		return nil, false
	}
	return o.Tests, true
}

// HasTests returns a boolean if a field has been set.
func (o *TestResult) HasTests() bool {
	if o != nil && o.Tests != nil {
		return true
	}

	return false
}

// SetTests gets a reference to the given int32 and assigns it to the Tests field.
func (o *TestResult) SetTests(v int32) {
	o.Tests = &v
}

// GetSkipped returns the Skipped field value if set, zero value otherwise.
func (o *TestResult) GetSkipped() int32 {
	if o == nil || o.Skipped == nil {
		var ret int32
		return ret
	}
	return *o.Skipped
}

// GetSkippedOk returns a tuple with the Skipped field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetSkippedOk() (*int32, bool) {
	if o == nil || o.Skipped == nil {
		return nil, false
	}
	return o.Skipped, true
}

// HasSkipped returns a boolean if a field has been set.
func (o *TestResult) HasSkipped() bool {
	if o != nil && o.Skipped != nil {
		return true
	}

	return false
}

// SetSkipped gets a reference to the given int32 and assigns it to the Skipped field.
func (o *TestResult) SetSkipped(v int32) {
	o.Skipped = &v
}

// GetError returns the Error field value if set, zero value otherwise.
func (o *TestResult) GetError() int32 {
	if o == nil || o.Error == nil {
		var ret int32
		return ret
	}
	return *o.Error
}

// GetErrorOk returns a tuple with the Error field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetErrorOk() (*int32, bool) {
	if o == nil || o.Error == nil {
		return nil, false
	}
	return o.Error, true
}

// HasError returns a boolean if a field has been set.
func (o *TestResult) HasError() bool {
	if o != nil && o.Error != nil {
		return true
	}

	return false
}

// SetError gets a reference to the given int32 and assigns it to the Error field.
func (o *TestResult) SetError(v int32) {
	o.Error = &v
}

// GetFailure returns the Failure field value if set, zero value otherwise.
func (o *TestResult) GetFailure() int32 {
	if o == nil || o.Failure == nil {
		var ret int32
		return ret
	}
	return *o.Failure
}

// GetFailureOk returns a tuple with the Failure field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetFailureOk() (*int32, bool) {
	if o == nil || o.Failure == nil {
		return nil, false
	}
	return o.Failure, true
}

// HasFailure returns a boolean if a field has been set.
func (o *TestResult) HasFailure() bool {
	if o != nil && o.Failure != nil {
		return true
	}

	return false
}

// SetFailure gets a reference to the given int32 and assigns it to the Failure field.
func (o *TestResult) SetFailure(v int32) {
	o.Failure = &v
}

// GetTime returns the Time field value if set, zero value otherwise.
func (o *TestResult) GetTime() float32 {
	if o == nil || o.Time == nil {
		var ret float32
		return ret
	}
	return *o.Time
}

// GetTimeOk returns a tuple with the Time field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetTimeOk() (*float32, bool) {
	if o == nil || o.Time == nil {
		return nil, false
	}
	return o.Time, true
}

// HasTime returns a boolean if a field has been set.
func (o *TestResult) HasTime() bool {
	if o != nil && o.Time != nil {
		return true
	}

	return false
}

// SetTime gets a reference to the given float32 and assigns it to the Time field.
func (o *TestResult) SetTime(v float32) {
	o.Time = &v
}

// GetTestResultCreated returns the TestResultCreated field value if set, zero value otherwise.
func (o *TestResult) GetTestResultCreated() time.Time {
	if o == nil || o.TestResultCreated == nil {
		var ret time.Time
		return ret
	}
	return *o.TestResultCreated
}

// GetTestResultCreatedOk returns a tuple with the TestResultCreated field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetTestResultCreatedOk() (*time.Time, bool) {
	if o == nil || o.TestResultCreated == nil {
		return nil, false
	}
	return o.TestResultCreated, true
}

// HasTestResultCreated returns a boolean if a field has been set.
func (o *TestResult) HasTestResultCreated() bool {
	if o != nil && o.TestResultCreated != nil {
		return true
	}

	return false
}

// SetTestResultCreated gets a reference to the given time.Time and assigns it to the TestResultCreated field.
func (o *TestResult) SetTestResultCreated(v time.Time) {
	o.TestResultCreated = &v
}

// GetExternalLinks returns the ExternalLinks field value if set, zero value otherwise.
func (o *TestResult) GetExternalLinks() string {
	if o == nil || o.ExternalLinks == nil {
		var ret string
		return ret
	}
	return *o.ExternalLinks
}

// GetExternalLinksOk returns a tuple with the ExternalLinks field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetExternalLinksOk() (*string, bool) {
	if o == nil || o.ExternalLinks == nil {
		return nil, false
	}
	return o.ExternalLinks, true
}

// HasExternalLinks returns a boolean if a field has been set.
func (o *TestResult) HasExternalLinks() bool {
	if o != nil && o.ExternalLinks != nil {
		return true
	}

	return false
}

// SetExternalLinks gets a reference to the given string and assigns it to the ExternalLinks field.
func (o *TestResult) SetExternalLinks(v string) {
	o.ExternalLinks = &v
}

// GetIsSuccess returns the IsSuccess field value if set, zero value otherwise.
func (o *TestResult) GetIsSuccess() bool {
	if o == nil || o.IsSuccess == nil {
		var ret bool
		return ret
	}
	return *o.IsSuccess
}

// GetIsSuccessOk returns a tuple with the IsSuccess field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetIsSuccessOk() (*bool, bool) {
	if o == nil || o.IsSuccess == nil {
		return nil, false
	}
	return o.IsSuccess, true
}

// HasIsSuccess returns a boolean if a field has been set.
func (o *TestResult) HasIsSuccess() bool {
	if o != nil && o.IsSuccess != nil {
		return true
	}

	return false
}

// SetIsSuccess gets a reference to the given bool and assigns it to the IsSuccess field.
func (o *TestResult) SetIsSuccess(v bool) {
	o.IsSuccess = &v
}

// GetHasSkip returns the HasSkip field value if set, zero value otherwise.
func (o *TestResult) GetHasSkip() bool {
	if o == nil || o.HasSkip == nil {
		var ret bool
		return ret
	}
	return *o.HasSkip
}

// GetHasSkipOk returns a tuple with the HasSkip field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetHasSkipOk() (*bool, bool) {
	if o == nil || o.HasSkip == nil {
		return nil, false
	}
	return o.HasSkip, true
}

// HasHasSkip returns a boolean if a field has been set.
func (o *TestResult) HasHasSkip() bool {
	if o != nil && o.HasSkip != nil {
		return true
	}

	return false
}

// SetHasSkip gets a reference to the given bool and assigns it to the HasSkip field.
func (o *TestResult) SetHasSkip(v bool) {
	o.HasSkip = &v
}

// GetTestSuites returns the TestSuites field value if set, zero value otherwise.
func (o *TestResult) GetTestSuites() []TestSuite {
	if o == nil || o.TestSuites == nil {
		var ret []TestSuite
		return ret
	}
	return *o.TestSuites
}

// GetTestSuitesOk returns a tuple with the TestSuites field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetTestSuitesOk() (*[]TestSuite, bool) {
	if o == nil || o.TestSuites == nil {
		return nil, false
	}
	return o.TestSuites, true
}

// HasTestSuites returns a boolean if a field has been set.
func (o *TestResult) HasTestSuites() bool {
	if o != nil && o.TestSuites != nil {
		return true
	}

	return false
}

// SetTestSuites gets a reference to the given []TestSuite and assigns it to the TestSuites field.
func (o *TestResult) SetTestSuites(v []TestSuite) {
	o.TestSuites = &v
}

// GetExternalLinksMap returns the ExternalLinksMap field value if set, zero value otherwise.
func (o *TestResult) GetExternalLinksMap() TestResult {
	if o == nil || o.ExternalLinksMap == nil {
		var ret TestResult
		return ret
	}
	return *o.ExternalLinksMap
}

// GetExternalLinksMapOk returns a tuple with the ExternalLinksMap field value if set, nil otherwise
// and a boolean to check if the value has been set.
func (o *TestResult) GetExternalLinksMapOk() (*TestResult, bool) {
	if o == nil || o.ExternalLinksMap == nil {
		return nil, false
	}
	return o.ExternalLinksMap, true
}

// HasExternalLinksMap returns a boolean if a field has been set.
func (o *TestResult) HasExternalLinksMap() bool {
	if o != nil && o.ExternalLinksMap != nil {
		return true
	}

	return false
}

// SetExternalLinksMap gets a reference to the given TestResult and assigns it to the ExternalLinksMap field.
func (o *TestResult) SetExternalLinksMap(v TestResult) {
	o.ExternalLinksMap = &v
}

func (o TestResult) MarshalJSON() ([]byte, error) {
	toSerialize := map[string]interface{}{}
	if o.TestResultId != nil {
		toSerialize["testResultId"] = o.TestResultId
	}
	if o.StageFk != nil {
		toSerialize["stageFk"] = o.StageFk
	}
	if o.Tests != nil {
		toSerialize["tests"] = o.Tests
	}
	if o.Skipped != nil {
		toSerialize["skipped"] = o.Skipped
	}
	if o.Error != nil {
		toSerialize["error"] = o.Error
	}
	if o.Failure != nil {
		toSerialize["failure"] = o.Failure
	}
	if o.Time != nil {
		toSerialize["time"] = o.Time
	}
	if o.TestResultCreated != nil {
		toSerialize["testResultCreated"] = o.TestResultCreated
	}
	if o.ExternalLinks != nil {
		toSerialize["externalLinks"] = o.ExternalLinks
	}
	if o.IsSuccess != nil {
		toSerialize["isSuccess"] = o.IsSuccess
	}
	if o.HasSkip != nil {
		toSerialize["hasSkip"] = o.HasSkip
	}
	if o.TestSuites != nil {
		toSerialize["testSuites"] = o.TestSuites
	}
	if o.ExternalLinksMap != nil {
		toSerialize["externalLinksMap"] = o.ExternalLinksMap
	}
	return json.Marshal(toSerialize)
}

type NullableTestResult struct {
	value *TestResult
	isSet bool
}

func (v NullableTestResult) Get() *TestResult {
	return v.value
}

func (v *NullableTestResult) Set(val *TestResult) {
	v.value = val
	v.isSet = true
}

func (v NullableTestResult) IsSet() bool {
	return v.isSet
}

func (v *NullableTestResult) Unset() {
	v.value = nil
	v.isSet = false
}

func NewNullableTestResult(val *TestResult) *NullableTestResult {
	return &NullableTestResult{value: val, isSet: true}
}

func (v NullableTestResult) MarshalJSON() ([]byte, error) {
	return json.Marshal(v.value)
}

func (v *NullableTestResult) UnmarshalJSON(src []byte) error {
	v.isSet = true
	return json.Unmarshal(src, &v.value)
}


