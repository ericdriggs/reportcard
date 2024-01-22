#!/bin/bash
set -x
#pre-requisite: run local-s3.sh
export SPRING_PROFILES_ACTIVE=dev
gradle bootRun -Si -Ds3.endpoint=http://localhost:9090/