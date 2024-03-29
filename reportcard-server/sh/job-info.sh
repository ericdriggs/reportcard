#!/usr/bin/env zsh

#log input
#set -x

echo "SHELL: $SHELL"
echo "BASH_VERSION: ${BASH_VERSION}"

typeset -A dict
#dict=(k1 v1 k2 v2 k3 v3)
dict[HOST]="host1"
dict[APPLICATION]="app1"
dict[PIPELINE]="pipeline1"
dict[ENV]="env1"

job_info=""
for k in "${(@k)dict}"; do
  job_info="${job_info}$k=$dict[$k],"
done

echo "job_info: $job_info"
