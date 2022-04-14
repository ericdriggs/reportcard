#!/bin/sh

set -x #todo: remove to hide password

#required args
: "${REPORTCARD_HOST:?REPORTCARD_HOST not set or empty}"
: "${REPORTCARD_USER:?REPORTCARD_USER not set or empty}"
: "${REPORTCARD_PASS:?REPORTCARD_PASS not set or empty}"

: "${SCM_ORG:?SCM_ORG not set or empty}"
: "${SCM_REPO:?SCM_REPO not set or empty}"
: "${SCM_BRANCH:?SCM_BRANCH not set or empty}"
: "${SCM_SHA:?SCM_SHA not set or empty}"

: "${CONTEXT_HOST:?CONTEXT_HOST not set or empty}"
: "${EXECUTION_STAGE:?EXECUTION_STAGE not set or empty}"

#required with defaults
EXECUTION_EXTERNAL_ID="${EXECUTION_EXTERNAL_ID:-$(uuidgen)}"
TEST_REPORT_REGEX="${TEST_REPORT_REGEX:-*.xml}"

#optional args which can be empty/null
CONTEXT_APPLICATION="${CONTEXT_APPLICATION:-}"
CONTEXT_PIPELINE="${CONTEXT_PIPELINE:-}"
EXTERNAL_LINKS_JSON="${EXTERNAL_LINKS_JSON:-{}"

#build curl command
IFS=$'\n'
REPORT_FILES=$(find $TEST_REPORT_PATH -name "$TEST_REPORT_REGEX" -maxdepth 1 )
unset IFS

# echo "REPORT_FILES:\n$REPORT_FILES"

REPORT_FILES_FLAGS=''
for item in $REPORT_FILES
do
#     NEW_FLAG = " --form 'files=@" + $item + "'"
    NEW_FLAG="--form 'files=@\"$item\"'"
    #NEW_FLAG="--form 'files=@\"/Users/eric.r.driggs/github/ericdriggs/reportcard/reportcard-server/src/test/resources/format-samples/sample-junit.xml\"'"
#     echo "NEW_FLAG: $NEW_FLAG\n"
    REPORT_FILES_FLAGS="$REPORT_FILES_FLAGS $NEW_FLAG"
done
# echo "REPORT_FILES_FLAGS${REPORT_FILES_FLAGS[*]}"

REPORT_METADATA=$(cat <<EOT
{
  \"org\": \"$SCM_ORG\",
  \"repo\": \"$SCM_REPO\",
  \"branch\": \"$SCM_BRANCH\",
  \"sha\": \"$SCM_SHA\",
  \"hostApplicationPipeline\": {
    \"host\": \"$CONTEXT_HOST\",
    \"application\": \"$CONTEXT_APPLICATION\",
    \"pipeline\": \"$CONTEXT_PIPELINE\"
  },
  \"externalExecutionId\": \"$EXECUTION_EXTERNAL_ID\",
  \"stage\": \"$EXECUTION_STAGE\",
  \"externalLinks\": $EXTERNAL_LINKS_JSON
}
EOT
)
# echo "\n\nREPORT_METADATA:\n$REPORT_METADATA\n\n"


CURL_CMD="curl \
--user $REPORTCARD_USER:$REPORTCARD_PASS \
--verbose \
--request POST "$REPORTCARD_HOST/api/v1/reports" \
--form reportMetaData=${REPORT_METADATA};type=application/json \
$REPORT_FILES_FLAGS"

echo "\n\n\n####  CURL_CMD  ###\n\n\n"
echo $CURL_CMD
# echo "\n\nCURL_CMD:\n$CURL_CMD\n\n"
echo "\n\n\n#######\n\n\n"

exit
eval $($CURL_CMD)