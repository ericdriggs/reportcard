
# TestCase

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**className** | **String** |  |  [optional]
**name** | **String** |  |  [optional]
**testCaseId** | **Long** |  |  [optional]
**testStatus** | [**TestStatusEnum**](#TestStatusEnum) |  |  [optional]
**testStatusFk** | **Integer** |  |  [optional]
**testSuiteFk** | **Long** |  |  [optional]
**time** | [**BigDecimal**](BigDecimal.md) |  |  [optional]


<a name="TestStatusEnum"></a>
## Enum: TestStatusEnum
Name | Value
---- | -----
SUCCESS | &quot;SUCCESS&quot;
SKIPPED | &quot;SKIPPED&quot;
FAILURE | &quot;FAILURE&quot;
ERROR | &quot;ERROR&quot;
FLAKY_FAILURE | &quot;FLAKY_FAILURE&quot;
RERUN_FAILURE | &quot;RERUN_FAILURE&quot;
FLAKY_ERROR | &quot;FLAKY_ERROR&quot;
RERUN_ERROR | &quot;RERUN_ERROR&quot;



