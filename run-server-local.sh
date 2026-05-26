#!/bin/bash
mise exec -- ./gradlew :reportcard-server:bootRun -Si 2>&1 | tee ./tmp/server.log
