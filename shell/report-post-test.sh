#!/bin/sh

set -x #todo: remove to hide password

REPORTCARD_HOST="http://localhost:8080" \
REPORTCARD_USER="TODO_USER" \
REPORTCARD_PASS="TODO_PASS" \
SCM_ORG="ORG1" \
SCM_REPO="REPO1" \
SCM_BRANCH="BRANCH1" \
SCM_SHA="SHA1" \
CONTEXT_HOST="HOST1" \
EXECUTION_STAGE="STAGE1" \
TEST_REPORT_PATH="../test-data-generator/build/surefire-reports" \
CONTEXT_APPLICATION="APP1" \
CONTEXT_PIPELINE="PIPELINE1" \
bash -c './report-post.sh'

#TODO: EXTERNAL_LINKS_JSON