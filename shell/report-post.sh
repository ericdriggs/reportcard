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

: "${STAGE:?STAGE not set or empty}"

#optional with defaults
CONTEXT_METADATA="${CONTEXT_METADATA:-{}"
RUN_REFERENCE="${RUN_REFERENCE:-$(uuidgen)}"
TEST_REPORT_REGEX="${TEST_REPORT_REGEX:-*.xml}"
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

STAGE_DETAILS=$(cat <<EOT
{
  \"org\": \"$SCM_ORG\",
  \"repo\": \"$SCM_REPO\",
  \"branch\": \"$SCM_BRANCH\",
  \"sha\": \"$SCM_SHA\",
  \"metadata\": \"$METADATA\",
  \"runReference\": \"RUN_REFERENCE\",
  \"stage\": \"$STAGE\",
  \"externalLinks\": $EXTERNAL_LINKS_JSON
}
EOT
)


CURL_CMD="curl \
--user $REPORTCARD_USER:$REPORTCARD_PASS \
--verbose \
--request POST "$REPORTCARD_HOST/v1/api/reports" \
--form stageDetails=${STAGE_DETAILS};type=application/json \
$REPORT_FILES_FLAGS"

echo "\n\n\n####  CURL_CMD  ###\n\n\n"
echo $CURL_CMD
# echo "\n\nCURL_CMD:\n$CURL_CMD\n\n"
echo "\n\n\n#######\n\n\n"

exit
eval $($CURL_CMD)