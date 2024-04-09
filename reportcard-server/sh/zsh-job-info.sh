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

get_job_info() {
  set -x
  typeset -A dict
  [ -z "$HOST" ]        && dict['HOST']=$HOST
  [ -z "$APPLICATION" ] && dict['APPLICATION']=$APPLICATION
  [ -z "$PIPELINE" ]    && dict['$PIPELINE']=$PIPELINE
  [ -z "$ENV" ]         && dict['ENV']=$ENV

  job_info=""
  for k in "${(@k)dict}"; do
    job_info="${job_info}$k=$dict[$k],"
  done

  echo $job_info
  set +x
}

job_info=$(get_job_info)

echo "job_info: $job_info"
