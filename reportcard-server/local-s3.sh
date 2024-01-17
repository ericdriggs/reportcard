#!/bin/bash
set -x
export root=~/s3mock
export retainFilesOnExit=true
export debug=true

#using s3mock since localstack community doesn't support persistence
docker run -p 9090:9090 -p 9191:9191 -e initialBuckets=testbucket -e debug=true -t adobe/s3mock