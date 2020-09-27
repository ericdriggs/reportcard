#!/bin/bash

java -jar swagger-codegen-cli.jar generate \
  -i http://localhost:8080/v2/api-docs \
  --api-package com.ericdriggs.reportcard.client.api \
  --model-package com.ericdriggs.reportcard.client.api \
  --invoker-package com.ericdriggs.reportcard.client.invoker \
  --group-id com.ericdriggs \
  --artifact-id reportcard-client \
  --artifact-version 0.0.1-SNAPSHOT \
  -l java \
  --library resttemplate \
  -o .