#!/usr/bin/env bash

echo "SHELL: $SHELL"
echo "BASH_VERSION: ${BASH_VERSION}"

declare -A dict

dict[HOST]="host1"
dict[APPLICATION]="app1"
dict[PIPELINE]="pipeline1"
dict[ENV]="env1"

job_info=""
for k in "${!dict[@]}"
do
  job_info="${job_info}$k=${dict[$k]},"
done

echo "job_info: $job_info"
