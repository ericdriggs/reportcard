#!/bin/bash
set -x
#pre-requisite: run local-s3.sh
export S3_ENDPOINT=http://localhost:9091
export SPRING_PROFILES_ACTIVE=dev
gradle bootRun -Si