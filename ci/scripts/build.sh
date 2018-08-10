#!/bin/bash -eux

pushd dp-performance-reporter
  mvn -DskipTests=true clean package dependency:copy-dependencies
  cp -r Dockerfile.concourse target/* ../build/
popd
