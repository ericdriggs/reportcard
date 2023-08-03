#!/bin/bash
set -x
export root=~/s3mock
export retainFilesOnExit=true
export debug=true
docker run -p 9090:9090 -p 9191:9191 -e -t adobe/s3mock