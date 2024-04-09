#!/usr/bin/env bash
#requires bash >=4.0 so if running on osx, will need to run `brew install bash` first

set -x

: REPORTCARD_SERVER="${REPORTCARD_SERVER:-http://localhost:8080/}"
: GIT_COMPANY="${GIT_COMPANY:-company1}"
: LABEL="${LABEL:-cucumber_html}"
: STORAGE_TYPE="${STORAGE_TYPE:-html}"
: STAGE="${STAGE:-default}"
: INDEX_FILE="${INDEX_FILE:-overview-features.html}"

: "${GIT_ORG:?GIT_ORG not set or empty}"
: "${GIT_REPO:?GIT_REPO not set or empty}"
: "${GIT_BRANCH:?GIT_BRANCH not set or empty}"
: "${COMMIT_SHA:?COMMIT_SHA not set or empty}"

get_job_info() {
  set +x
  typeset -A dict
  [ -z "$HOST" ]        && dict['HOST']=$HOST
  [ -z "$APPLICATION" ] && dict['APPLICATION']=$APPLICATION
  [ -z "$PIPELINE" ]    && dict['PIPELINE']=$PIPELINE
  [ -z "$ENV" ]         && dict['ENV']=$ENV

  job_info=""
  for k in "${!dict[@]}"
  do
    job_info="${job_info}$k=${dict[$k]},"
  done

  echo $job_info
  set -x
}

job_info=$(get_job_info)

tar --strip-components 2 --disable-copyfile --exclude='.DS_Store' -cvzf cucumber-html-reports.tar.gz target/cucumber-html-reports/

tar --strip-components 3 --disable-copyfile --exclude='.DS_Store' -cvzf junit.tar.gz build/test-results/test/
#tar --strip-components 2 --disable-copyfile --exclude='.DS_Store' -cvzf junit.tar.gz build/karate-reports/

curl -X 'POST' \
  "${REPORTCARD_SERVER}/v1/api/storage/label/${LABEL}/tar.gz?company=${GIT_COMPANY}&org=${GIT_ORG}&repo=${GIT_REPO}&branch=${GIT_BRANCH}&sha=${COMMIT_SHA}&stage=${STAGE}&jobInfo=${JOB_INFO}&storageType=${STORAGE_TYPE}" \
  -H 'accept: */*' \
  -H 'Content-Type: multipart/form-data' \
  -F 'junit.tar.gz=@junit.tar.gz;type=application/x-gzip' \
  -F 'reports.tar.gz=@cucumber-html-reports.tar.gz;type=application/x-gzip'

